package com.magnuson.xen.faulttolerence;

import com.magnuson.xen.*;
import java.util.*;

import org.apache.log4j.Logger;

public class PhysicalMachineAndNetworkDeviceFaultTolerenceBalancer implements Balancer {

	static Logger log = Logger.getLogger(PhysicalMachineAndNetworkDeviceFaultTolerenceBalancer.class);

	private FaultTolerentQueryInterface faultQuery;
	private XenQueryHandlerInterface xenQuery;


	public PhysicalMachineAndNetworkDeviceFaultTolerenceBalancer(FaultTolerentQueryInterface fq, XenQueryHandlerInterface xq){
		this.faultQuery=fq;
		this.xenQuery=xq;
	}

	public List<MigrationDecision> calculate(){
		LinkedList<MigrationDecision> migrations = new LinkedList<MigrationDecision>();
		List<FaultSet> faultSets = faultQuery.getFaultSets();
		for(FaultSet fs: faultSets){
			migrations.addAll(calculate(fs));
		}
		return migrations;
	}

	private PhysicalMachine getLeastRisk(HashMap<PhysicalMachine, Integer> currentMapping, FaultSet fs){
		PhysicalMachine lowestRisk = null;
		double lowestRiskValue = Double.MAX_VALUE;
		for(PhysicalMachine pm: currentMapping.keySet()){
			if(pm.getRiskPlus(fs, currentMapping.get(pm)) < lowestRiskValue){
				lowestRisk = pm;
				lowestRiskValue = pm.getRiskPlus(fs, currentMapping.get(pm));
			}
		}
		return lowestRisk;
	}

	private PhysicalMachine getMostRisk(HashMap<PhysicalMachine, Integer> currentMapping, FaultSet fs){
		PhysicalMachine mostRisk = null;
		double mostRiskValue = Double.MIN_VALUE;
		for(PhysicalMachine pm: currentMapping.keySet()){
			if(pm.getRiskPlus(fs, currentMapping.get(pm)) > mostRiskValue){
				mostRisk = pm;
				mostRiskValue = pm.getRiskPlus(fs, currentMapping.get(pm));
			}
		}
		return mostRisk;
	}

	private List<MigrationDecision> calculate(FaultSet fs){
		LinkedList<MigrationDecision> migrations = new LinkedList<MigrationDecision>();

		//make a tally list for each physical machine
		HashMap<PhysicalMachine,Integer> currentMapping = new HashMap<PhysicalMachine,Integer>();
		for(PhysicalMachine pm: xenQuery.getPhysicalMachines()){
			if(pm.getSwitch().hasInternetConnection()){
				currentMapping.put(pm, 0);
			}
		}

		//tally number of vms in set per machine
		for(VirtualMachine vm: fs.getVirtualMachines()){
			PhysicalMachine pm = xenQuery.getPhysicalMachine(vm.getPhysicalMachineMACAddress());
			//add to currently assigned mapping if machine is up
			if(pm!=null && pm.getSwitch().hasInternetConnection()){
				currentMapping.put(pm, currentMapping.get(pm)+1);
			}
		}

		log.debug("Beginning Balancing Kernel");
		LinkedList<VirtualMachine> migrated = new LinkedList<VirtualMachine>();

		while(true){
			PhysicalMachine overloaded = getMostRisk(currentMapping, fs);
			PhysicalMachine underloaded = getLeastRisk(currentMapping, fs);

			
			if(overloaded==null || underloaded==null || overloaded.equals(underloaded)){
				log.trace("Only 1 or 0 PMs available");
				break;
			}
			
			double overloadedRisk = overloaded.getRiskPlus(fs, currentMapping.get(overloaded));
			double underloadedRisk = underloaded.getRiskPlus(fs, currentMapping.get(underloaded));
			
			if(overloaded.getRiskPlus(fs, currentMapping.get(overloaded))==underloaded.getRiskPlus(fs, currentMapping.get(underloaded))){
				log.trace("Max and Min risk equal, done balancing");
				break;
			}

			log.trace("Max Risk: "+overloadedRisk);
			log.trace("Min Risk: "+underloadedRisk);

			
			//if lowers total risk make the migration...
			if((overloaded.getRiskPlus(fs, currentMapping.get(overloaded)) + underloaded.getRiskPlus(fs, currentMapping.get(underloaded))) <=
			(overloaded.getRiskPlus(fs, currentMapping.get(overloaded)-1) + underloaded.getRiskPlus(fs, currentMapping.get(underloaded)+1))) {
				break;
			}
			
			if(underloaded.getRiskPlus(fs, currentMapping.get(underloaded))<0 || overloaded.getRiskPlus(fs, currentMapping.get(overloaded))<0){
				log.error("Risk calculated < 0, THIS SHOULD NEVER HAPPEN!");
			}

			VirtualMachine donation = getMigrationCandidate(overloaded, fs, migrated, migrations);
			if(donation==null){
				log.error("Max Risk: "+overloaded.getRiskPlus(fs, currentMapping.get(overloaded)));
				log.error("Min Risk: "+underloaded.getRiskPlus(fs, currentMapping.get(underloaded)));
				log.error(" ");
			}
			currentMapping.put(overloaded, currentMapping.get(overloaded)-1);
			currentMapping.put(underloaded, currentMapping.get(underloaded)+1);

			log.trace("Donating From: "+overloaded+" "+donation);

			//alter migration plan if this has already been scheduled for migration
			boolean existingMigration = false;
			for(MigrationDecision md: migrations){
				if(md.getVirtualMachine().equals(donation)){
					log.info("Existing migration modified");
					md.setPhysicalDestination(underloaded);
					existingMigration = true;
					break;
				}
			}
			if(!existingMigration){
				migrations.add(new MigrationDecision(donation, xenQuery.getPhysicalMachine(donation.getPhysicalMachineMACAddress()), underloaded));
				migrated.add(donation);			
			}

			log.trace("One rebalance iteration done");
		}

		log.trace(""+migrations.size()+" total migrations");
		return migrations;
	}

	public List<MigrationDecision> calculateAndMigrate(){
		List<MigrationDecision> migrations = calculate();
		migrate(migrations);
		return migrations;
	}

	private void migrate(List<MigrationDecision> migrations){
		for(MigrationDecision migration: migrations){
			xenQuery.migrateVirtualMachine(migration.getVirtualMachine(), migration.getPhysicalSource(), migration.getPhysicalDestination());
		}
	}

	private VirtualMachine getMigrationCandidate(PhysicalMachine pm, FaultSet fs, List<VirtualMachine> migrated, List<MigrationDecision> migrations){
		List<VirtualMachine> candidateSet = fs.getVirtualMachines();
		for(VirtualMachine vm: pm.getVirtualMachines()){
			if(candidateSet.contains(vm) && !migrated.contains(vm)){
				return vm;
			}
		}

		//no VM existing on this machine can be moved, pick one from the VMs to be migrated here
		for(MigrationDecision md: migrations){
			if(md.getPhysicalDestination().equals(pm)){
				return md.getVirtualMachine();
			}
		}

		log.error("null VM returned, no migration candidate found.  THIS SHOULD NEVER HAPPEN!");
		return null;
	}

}
