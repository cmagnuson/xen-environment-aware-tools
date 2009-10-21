package com.magnuson.xen.faulttolerence;

import com.magnuson.xen.*;
import java.util.*;

import org.apache.log4j.Logger;

public class FaultTolerenceBalancer {

	static Logger log = Logger.getLogger(FaultTolerenceBalancer.class);

	private FaultTolerentQueryInterface faultQuery;
	private XenQueryHandlerInterface xenQuery;

	private FaultTolerenceBalancer(){}

	public FaultTolerenceBalancer(FaultTolerentQueryInterface fq, XenQueryHandlerInterface xq){
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

	private List<MigrationDecision> calculate(FaultSet fs){
		LinkedList<MigrationDecision> migrations = new LinkedList<MigrationDecision>();

		//make a tally list for each physical machine
		HashMap<PhysicalMachine,Integer> currentMapping = new HashMap<PhysicalMachine,Integer>();
		for(PhysicalMachine pm: xenQuery.getPhysicalMachines()){
			currentMapping.put(pm, 0);
		}

		//tally number of vms in set per machine
		for(VirtualMachine vm: fs.getVirtualMachines()){
			PhysicalMachine pm = xenQuery.getPhysicalMachine(vm.getPhysicalMachineMACAddress());
			currentMapping.put(pm, currentMapping.get(pm)+1);
		}

		int freeMachines = xenQuery.getPhysicalMachines().size();
		int vmCount = countLiveVms(fs);
		double idealBalancing = (double)vmCount/(double)freeMachines;
		log.debug("Beginning Balancing Kernel, initial ideal balancing: "+idealBalancing);
		VirtualMachine donation=null;
		LinkedList<VirtualMachine> migrated = new LinkedList<VirtualMachine>();

		while(!currentMapping.isEmpty()){
			Iterator<PhysicalMachine> it = currentMapping.keySet().iterator();
			while(it.hasNext()){
				PhysicalMachine pm = it.next();

				log.trace(pm.toString()+" value: "+currentMapping.get(pm)+" ideal value: "+idealBalancing);
				if(closeEnough(currentMapping.get(pm), idealBalancing)){
					freeMachines--;
					vmCount-=currentMapping.get(pm);
					it.remove();

					if(vmCount==0 || freeMachines==0){
						break;
					}
					else{
						idealBalancing = (double)vmCount/(double)freeMachines;
						log.debug("Removing "+pm+" from balancing set, new ideal balancing factor: "+idealBalancing);
						continue;
					}
				}
				else{
					if(currentMapping.get(pm)+.5>idealBalancing && donation==null){
						donation = getMigrationCandidate(pm, fs, migrated);
						currentMapping.put(pm, currentMapping.get(pm)-1);

						log.trace("Donating From: "+pm+" "+donation);
					}
				}
				if(currentMapping.get(pm)+.5<idealBalancing && donation!=null){
					currentMapping.put(pm, currentMapping.get(pm)+1);
					migrations.add(new MigrationDecision(donation, xenQuery.getPhysicalMachine(donation.getPhysicalMachineMACAddress()), pm));
					migrated.add(donation);

					log.trace("Accepting Donation: "+pm+" "+donation);

					donation = null;
				}
			}
			log.trace("One rebalance iteration done, "+freeMachines+" machines left, "+vmCount+" VMs left");
		}

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

	private boolean closeEnough(int actual, double ideal){
		//decides if the actual vm count is close enough to idea to not make any changes
		if(Math.abs(actual-ideal)<1){
			return true;
		}
		return false;
	}

	private VirtualMachine getMigrationCandidate(PhysicalMachine pm, FaultSet fs, List<VirtualMachine> migrated){
		List<VirtualMachine> candidateSet = fs.getVirtualMachines();
		for(VirtualMachine vm: pm.getVirtualMachines()){
			if(candidateSet.contains(vm) && !migrated.contains(vm)){
				return vm;
			}
		}
		return null; //THIS SHOULD NEVER HAPPEN!
	}

	private int countLiveVms(FaultSet fs){
		int count = 0;
		for(VirtualMachine vm: fs.getVirtualMachines()){
			if(xenQuery.getVirtualMachine(vm.getMACAddress())!=null){
				count++;
			}
		}
		return count;
	}
}
