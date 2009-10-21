package com.magnuson.xen.faulttolerence.test;

import junit.framework.TestCase;
import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.*;

import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class FaultTolerenceBalancerTests extends TestCase {

	private static Logger log = Logger.getLogger(FaultTolerenceBalancerTests.class);
		
	private XenQueryHandlerInterface xq;
	private FaultTolerentQueryInterface ft;
	private FaultTolerenceBalancer balancer;

	private static final int RANDOM_RUNS = 1000;
	private static final int RANDOM_VM_AVG = 100;
	private static final int RANDOM_PM_AVG = 10;
	private static final int RANDOM_FS_AVG = 4;
	private static final double RANDOM_IN_FS_PROB = .3;


	protected void setUp() throws Exception {
		super.setUp();

		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);

		xq = new SimulatedQueryHandler();
		ft = new SimulatedFaultTolerentQueryHandler();
		balancer = new FaultTolerenceBalancer(ft,xq);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRandomEnvironment(){
		for(int run=0; run<RANDOM_RUNS; run++){
			xq = new SimulatedQueryHandler();
			ft = new SimulatedFaultTolerentQueryHandler();
			balancer = new FaultTolerenceBalancer(ft,xq);

			for(int i=0; i<RANDOM_PM_AVG*2; i++){
				//50/50 chance to make new machine
				if(Math.random()>.5){
					PhysicalMachine pm = new PhysicalMachine(("P"+i));
					xq.addPhysicalMachine(pm);
				}
			}
			for(int i=0; i<RANDOM_VM_AVG*2; i++){
				if(Math.random()>.5){
					VirtualMachine vm = new VirtualMachine("V"+i);
					PhysicalMachine pm = getRandomPhysicalMachine();
					if(pm!=null){
						xq.addVirtualMachine(vm, pm);
					}
				}
			}

			for(int i=0; i<RANDOM_FS_AVG*2; i++){
				if(Math.random()>.5){
					ft.addFaultSet(new FaultSet());
				}
			}


			for(VirtualMachine vm: xq.getVirtualMachines()){
				if(Math.random()<RANDOM_IN_FS_PROB){
					FaultSet fs = getRandomFaultSet();
					if(fs!=null){
						fs.addVirtualMachine(vm);
					}
				}
			}

			balancer.calculateAndMigrate();
			for(FaultSet fs: ft.getFaultSets()){
				assertBalanced(fs);
			}
			assert(balancer.calculateAndMigrate()==null);		
		}
		
	}


	public void testPlannedMigrateDecision(){
		xq = new SimulatedQueryHandler();
		ft = new SimulatedFaultTolerentQueryHandler();
		balancer = new FaultTolerenceBalancer(ft,xq);

		PhysicalMachine pm = new PhysicalMachine("A");
		PhysicalMachine pm2 = new PhysicalMachine("B");
		PhysicalMachine pm3 = new PhysicalMachine("C");
		PhysicalMachine pm4 = new PhysicalMachine("D");

		VirtualMachine vm1 = new VirtualMachine("V1");
		VirtualMachine vm2 = new VirtualMachine("V2");
		VirtualMachine vm3 = new VirtualMachine("V3");
		VirtualMachine vm4 = new VirtualMachine("V4");
		VirtualMachine vm5 = new VirtualMachine("V5");
		VirtualMachine vm6 = new VirtualMachine("V6");
		VirtualMachine vm7 = new VirtualMachine("V7");
		VirtualMachine vm8 = new VirtualMachine("V8");
		VirtualMachine vm9 = new VirtualMachine("V9");
		VirtualMachine vm10 = new VirtualMachine("V10");

		xq.addPhysicalMachine(pm);
		xq.addPhysicalMachine(pm2);
		xq.addPhysicalMachine(pm3);
		xq.addPhysicalMachine(pm4);
		xq.addVirtualMachine(vm1, pm);
		xq.addVirtualMachine(vm2, pm);
		xq.addVirtualMachine(vm3, pm);
		xq.addVirtualMachine(vm4, pm2);
		xq.addVirtualMachine(vm5, pm2);
		xq.addVirtualMachine(vm6, pm3);
		xq.addVirtualMachine(vm7, pm3);
		xq.addVirtualMachine(vm8, pm3);
		xq.addVirtualMachine(vm9, pm3);
		xq.addVirtualMachine(vm10, pm3);

		FaultSet fs = new FaultSet();
		fs.addVirtualMachine(vm1);
		fs.addVirtualMachine(vm2);
		fs.addVirtualMachine(vm3);
		fs.addVirtualMachine(vm4);
		fs.addVirtualMachine(vm5);
		fs.addVirtualMachine(vm6);
		fs.addVirtualMachine(vm7);
		fs.addVirtualMachine(vm8);
		fs.addVirtualMachine(vm9);
		fs.addVirtualMachine(vm10);
		ft.addFaultSet(fs);

		balancer.calculateAndMigrate();
		assertBalanced(fs);

		assert(balancer.calculateAndMigrate()==null);
	}

	
	private PhysicalMachine getRandomPhysicalMachine(){
		//in case by randomness there are no PMs
		if(xq.getPhysicalMachines().size()==0){
			return null;
		}
		return xq.getPhysicalMachines().get((int)Math.round(Math.floor(Math.random()*xq.getPhysicalMachines().size())));
	}
	
	private FaultSet getRandomFaultSet(){
		//in case by randomness there are no FSs
		if(ft.getFaultSets().size()==0){
			return null;
		}
		return ft.getFaultSets().get((int)Math.round(Math.floor(Math.random()*ft.getFaultSets().size())));
	}
	
	private void assertBalanced(FaultSet fs){
		//make a tally list for each physical machine
		HashMap<PhysicalMachine,Integer> currentMapping = new HashMap<PhysicalMachine,Integer>();
		for(PhysicalMachine pm: xq.getPhysicalMachines()){
			currentMapping.put(pm, 0);
		}

		//tally number of vms in set per machine
		for(VirtualMachine vm: fs.getVirtualMachines()){
			PhysicalMachine pm = xq.getPhysicalMachine(vm.getPhysicalMachineMACAddress());
			currentMapping.put(pm, currentMapping.get(pm)+1);
		}

		int freeMachines = xq.getPhysicalMachines().size();
		int vmCount = countLiveVms(fs);
		double ideal = (double)vmCount/(double)freeMachines;

		for(Integer val: currentMapping.values()){
			assert(closeEnough(val, ideal));
		}
	}

	private boolean closeEnough(int actual, double ideal){
		//decides if the actual vm count is close enough to idea to not make any changes
		if(Math.abs(actual-ideal)<1){
			return true;
		}
		return false;
	}

	private int countLiveVms(FaultSet fs){
		int count = 0;
		for(VirtualMachine vm: fs.getVirtualMachines()){
			if(xq.getVirtualMachine(vm.getMACAddress())!=null){
				count++;
			}
		}
		return count;
	}

}
