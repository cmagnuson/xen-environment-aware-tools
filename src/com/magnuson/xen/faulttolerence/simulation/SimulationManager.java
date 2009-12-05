package com.magnuson.xen.faulttolerence.simulation;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.*;
import com.magnuson.xen.faulttolerence.simulation.events.*;
import java.io.*;

public class SimulationManager {

	public static final Level DEBUG_LEVEL = Level.INFO;
	
	public static final long SECONDS = 10;
	public static final long MINUTES = SECONDS*60;
	public static final long HOURS = MINUTES*60;
	public static final long DAYS = HOURS*24;
	public static final long YEARS = DAYS*365;
	
	public static final long RUNNING_TIME = 10*YEARS;
	
	public static final int TOTAL_VMS = 20;
	public static final int TOTAL_PHYSICAL_MACHINES = 10;
	public static final long PHYSICAL_REBOOT_TIME = 1*HOURS;
	public static final long AVG_VIRTUAL_REBOOT_TIME = 10*MINUTES;
	public static final long AUTOMATIC_MIGRATE_POLL_RATE = 30*SECONDS;
	public static final long MANUAL_MIGRATE_POLL_RATE = RUNNING_TIME; //1*YEARS;
	public static final long MEAN_HARDWARE_UPTIME = 42*DAYS;
	public static final double SERVICE_UP_PCT = .90;
	
	//for 99.9% uptime average (assuming only server failure) is 8.76 hours down per year
	//to simulate, lets try reboot_time of 1 hour, is 42 days between failures
	
	
	public static XenQueryHandlerInterface xq = new SimulatedQueryHandler();
	public static FaultTolerentQueryInterface ft = new SimulatedFaultTolerentQueryHandler();
	public static FaultTolerenceBalancer balancer = new FaultTolerenceBalancer(ft,xq);
	public static boolean faultManagingEnabled = true;
	
	static Logger log = Logger.getLogger(SimulationManager.class);
	
	//TODO: find balancing bug that is causing infinite loop on certain simulation runs
	//TODO:  fix NPE occuring in Statistics:34 when running w/ millisecond precision (SECONDS=1000)
	
	public static void main(String[] args) {
		initLogging();
		
		log.info("Beginning Managed Run");
		Timeline t = new Timeline();
		initData(t);
		Statistics managed = t.executeAll();
		
		log.info("Beginning Unmanaged Run");
		faultManagingEnabled = false;
		t = new Timeline();
		initData(t);
		Statistics unmanaged = t.executeAll();
		
		//write unmanaged and managed to disk
		File ff = new File("managed.csv");
		File uf = new File("unmanaged.csv");
		try{
		FileWriter fw = new FileWriter(ff);
		fw.write(managed.getCsv());
		fw.close();
		
		fw = new FileWriter(uf);
		fw.write(unmanaged.getCsv());
		fw.close();
		}
		catch(IOException ioe){
			log.error("IO Exception Writing Statistics to Disk", ioe);
		}
		
		log.info("Managed VM Uptime: "+managed.getAvgVmUptime()*100);
		log.info("Unmanaged VM Uptime: "+unmanaged.getAvgVmUptime()*100);
		
		log.info("Managed Service Uptime: "+managed.getAvgServiceUptime()*100);
		log.info("Unmanaged Service Uptime: "+unmanaged.getAvgServiceUptime()*100);
		log.info("Difference: "+(managed.getAvgServiceUptime()-unmanaged.getAvgServiceUptime())*100);
	}

	//set up initial systems, all vms and machines live and properly balanced
	public static void initData(Timeline t){
		xq = new SimulatedQueryHandler();
		ft = new SimulatedFaultTolerentQueryHandler();
		balancer = new FaultTolerenceBalancer(ft,xq);

		FaultSet fs = new FaultSet();
		ft.addFaultSet(fs);

		for(int i=0; i<TOTAL_PHYSICAL_MACHINES; i++){
			PhysicalMachine pm = new PhysicalMachine(("P"+i));
			xq.addPhysicalMachine(pm);
			t.addEvent(new MachineDownEvent(pm), (long)(Math.random()*2*SimulationManager.MEAN_HARDWARE_UPTIME));
		}
		for(int i=0; i<TOTAL_VMS; i++){
			VirtualMachine vm = new VirtualMachine("V"+i);
			PhysicalMachine pm = xq.getPhysicalMachines().get(0);
			xq.addVirtualMachine(vm, pm);
			fs.addVirtualMachine(vm);
		}

		balancer.calculateAndMigrate();
		
		t.addEvent(new BalancingEvent(), 0);
		t.addEvent(new TerminalEvent(), RUNNING_TIME);
	}

	public static void initLogging(){
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(DEBUG_LEVEL);
	}
}
