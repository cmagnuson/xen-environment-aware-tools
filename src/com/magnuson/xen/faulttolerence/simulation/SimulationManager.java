package com.magnuson.xen.faulttolerence.simulation;

import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.*;
import com.magnuson.xen.faulttolerence.simulation.events.*;

public class SimulationManager {

	public static final int SECONDS = 1;
	public static final int MINUTES = SECONDS*60;
	public static final int HOURS = MINUTES*60;
	public static final int DAYS = HOURS*24;
	public static final int YEARS = DAYS*365;
	
	public static final long RUNNING_TIME = 3*YEARS;
	
	public static final int TOTAL_VMS = 10;
	public static final int TOTAL_PHYSICAL_MACHINES = 5;
	public static final int PHYSICAL_REBOOT_TIME = 10*MINUTES;
	public static final int VIRTUAL_REBOOT_TIME = 45*SECONDS;
	public static final int AUTOMATIC_MIGRATE_POLL_RATE = 30*SECONDS;
	public static final int MANUAL_MIGRATE_POLL_RATE = 1*DAYS;
	
	
	public static XenQueryHandlerInterface xq = new SimulatedQueryHandler();
	public static FaultTolerentQueryInterface ft = new SimulatedFaultTolerentQueryHandler();
	public static FaultTolerenceBalancer balancer = new FaultTolerenceBalancer(ft,xq);
	public static boolean faultManagingEnabled = true;
	
	public static void main(String[] args) {
		initData();
		Timeline t = new Timeline();
		t.addEvent(new MachineDownEvent(), 0);
		t.addEvent(new TerminalEvent(), RUNNING_TIME);
		t.executeAll();
		
		initData();
		t = new Timeline();
		t.addEvent(new MachineDownEvent(), 0);
		t.addEvent(new TerminalEvent(), RUNNING_TIME);
		t.executeAll();
		
	}

	//set up initial systems, all vms and machines live and properly balanced
	public static void initData(){
		xq = new SimulatedQueryHandler();
		ft = new SimulatedFaultTolerentQueryHandler();
		balancer = new FaultTolerenceBalancer(ft,xq);

		FaultSet fs = new FaultSet();
		ft.addFaultSet(fs);

		for(int i=0; i<TOTAL_PHYSICAL_MACHINES; i++){
			PhysicalMachine pm = new PhysicalMachine(("P"+i));
			xq.addPhysicalMachine(pm);
		}
		for(int i=0; i<TOTAL_VMS; i++){
			VirtualMachine vm = new VirtualMachine("V"+i);
			PhysicalMachine pm = xq.getPhysicalMachines().get(0);
			xq.addVirtualMachine(vm, pm);
			fs.addVirtualMachine(vm);
		}

		balancer.calculateAndMigrate();
	}

}
