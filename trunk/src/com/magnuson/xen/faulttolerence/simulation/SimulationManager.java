package com.magnuson.xen.faulttolerence.simulation;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.*;
import com.magnuson.xen.faulttolerence.simulation.events.*;
import java.io.*;
import java.util.*;

public class SimulationManager {

	public static final Level DEBUG_LEVEL = Level.WARN;

	public static final long SECONDS = 1;
	public static final long MINUTES = SECONDS*60;
	public static final long HOURS = MINUTES*60;
	public static final long DAYS = HOURS*24;
	public static final long YEARS = DAYS*365;

	public static final long RUNNING_TIME = 3*YEARS;
	public static final int	NUMBER_OF_RUNS = 50;

	public static int TOTAL_VMS = 10;
	public static int TOTAL_PHYSICAL_MACHINES = 10;
	public static final long PHYSICAL_REBOOT_TIME = 1*HOURS;
	public static final long SWITCH_REBOOT_TIME = 30*MINUTES;
	public static final long AVG_VIRTUAL_REBOOT_TIME = 10*MINUTES;
	public static final long AUTOMATIC_MIGRATE_POLL_RATE = 30*SECONDS;
	public static final long MANUAL_MIGRATE_POLL_RATE = RUNNING_TIME; //1*YEARS;
	public static final long MEAN_HARDWARE_UPTIME = 42*DAYS;
	public static double SERVICE_UP_PCT = .79;

	public static final double SWITCH_MTBF = .999;
	public static final double PM_MTBF = .999;
	//for 99.9% uptime average (assuming only server failure) is 8.76 hours down per year
	//to simulate, lets try reboot_time of 1 hour, is 42 days between failures


	public static XenQueryHandlerInterface xq = new SimulatedQueryHandler();
	public static FaultTolerentQueryInterface ft = new SimulatedFaultTolerentQueryHandler();
	public static PhysicalMachineFaultTolerenceBalancer balancer = new PhysicalMachineFaultTolerenceBalancer(ft,xq);	
	public static PhysicalMachineAndNetworkDeviceFaultTolerenceBalancer netPhysBalancer = new PhysicalMachineAndNetworkDeviceFaultTolerenceBalancer(ft,xq);
	public static boolean faultManagingEnabled = true;
	public static boolean manageNetwork = false;

	static Logger log = Logger.getLogger(SimulationManager.class);


	public static void main(String[] args) {
		initLogging();


		//for(SERVICE_UP_PCT=.09; SERVICE_UP_PCT<1; SERVICE_UP_PCT+=.1){
		//for(TOTAL_PHYSICAL_MACHINES=8; TOTAL_PHYSICAL_MACHINES<=40; TOTAL_PHYSICAL_MACHINES+=2){
		//	for(TOTAL_VMS=2; TOTAL_VMS<41; TOTAL_VMS+=2){
		double unmanagedUp = 0;
		double managedUp = 0;
		double managedNetUp = 0;

		for(int i=0; i<NUMBER_OF_RUNS; i++){
			log.info("Beginning Managed Run");
			manageNetwork = false;
			faultManagingEnabled = true;
			Timeline t = new Timeline();
			initData(t);
			Statistics managed = t.executeAll();
			managedUp+=managed.getAvgServiceUptime();

			log.info("Beginning Unmanaged Run");
			faultManagingEnabled = false;
			manageNetwork = false;
			t = new Timeline();
			initData(t);
			Statistics unmanaged = t.executeAll();
			unmanagedUp+=unmanaged.getAvgServiceUptime();

			log.info("Beginning Net Managed Run");
			manageNetwork = true;
			faultManagingEnabled = true;
			t = new Timeline();
			initData(t);
			Statistics managedNet = t.executeAll();
			managedNetUp+=managedNet.getAvgServiceUptime();
		}

		managedUp /= (double)NUMBER_OF_RUNS;
		unmanagedUp /= (double)NUMBER_OF_RUNS;
		managedNetUp /= (double)NUMBER_OF_RUNS;


		log.warn(""+TOTAL_PHYSICAL_MACHINES+","+TOTAL_VMS+","+(unmanagedUp*100)+","+(managedUp*100)+","+(managedNetUp*100));
		//}
		//}
	}

	//set up initial systems, all vms and machines live and properly balanced
	public static void initData(Timeline t){
		xq = new SimulatedQueryHandler();
		ft = new SimulatedFaultTolerentQueryHandler();
		balancer = new PhysicalMachineFaultTolerenceBalancer(ft,xq);
		netPhysBalancer = new PhysicalMachineAndNetworkDeviceFaultTolerenceBalancer(ft,xq);

		FaultSet fs = new FaultSet();
		ft.addFaultSet(fs);

		LinkedList<Switch> switches = new LinkedList<Switch>();
		Switch swi = new Switch("Level-1", new InternetGateway(), SWITCH_MTBF);
		Switch swi2 = new Switch("Level-2", swi, SWITCH_MTBF);
		Switch swi3 = new Switch("Level-3", swi2, SWITCH_MTBF);
		switches.add(swi);
		switches.add(swi2);
		switches.add(swi3);

		for(Switch s: switches){
			t.addEvent(new SwitchDownEvent(s), (long)(Math.random()*2*(double)SimulationManager.YEARS/((1.0-swi.getMtbf())*SimulationManager.YEARS/SimulationManager.SWITCH_REBOOT_TIME)));
		}

		for(int i=0; i<TOTAL_PHYSICAL_MACHINES; i++){
			PhysicalMachine pm = new PhysicalMachine(("P"+i), switches.get((int)Math.floor(Math.random()*switches.size())), PM_MTBF);
			xq.addPhysicalMachine(pm);
			t.addEvent(new MachineDownEvent(pm), (long)(Math.random()*2*SimulationManager.MEAN_HARDWARE_UPTIME));
		}
		for(int i=0; i<TOTAL_VMS; i++){
			VirtualMachine vm = new VirtualMachine("V"+i);
			PhysicalMachine pm = xq.getPhysicalMachines().get(0);
			xq.addVirtualMachine(vm, pm);
			fs.addVirtualMachine(vm);
		}

		if(manageNetwork){
			t.addEvent(new BalancingEvent(netPhysBalancer), 0);
			netPhysBalancer.calculateAndMigrate();
		}
		else{
			t.addEvent(new BalancingEvent(balancer), 0);
			balancer.calculateAndMigrate();
		}

		t.addEvent(new TerminalEvent(), RUNNING_TIME);
	}

	public static void initLogging(){
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(DEBUG_LEVEL);
	}
}
