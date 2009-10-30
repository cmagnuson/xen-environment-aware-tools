package com.magnuson.xen.faulttolerence.simulation;

import java.util.*;
import org.apache.log4j.Logger;

import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.simulation.events.*;

public class Timeline {

	static Logger log = Logger.getLogger(Timeline.class);
	private PriorityQueue<Event> timeline = new PriorityQueue<Event>();
	private long currentTime = 0;
	private Statistics statistics = new Statistics();

	public Timeline(){}

	public void addEvent(Event e, long scheduledOffset){
		e.setExecutionTime(currentTime+scheduledOffset);
		if(timeline.contains(e)){
			return;
		}
		else{
			timeline.add(e);
		}
	}

	//returns true when done executing all events to TerminalEvent
	public boolean executeNextEvent(){
		Event e = timeline.poll();
		if(e==null){
			log.error("Out of events in timeline, THIS SHOULD NEVER HAPPEN!");
			return false;
		}

		log.debug("Executing: "+e);

		currentTime = e.getExecutionTime();
		updateStatistics();

		if(!(e instanceof TerminalEvent)){
			e.execute(this);
			return false;
		}
		else{
			return true;
		}
	}

	public Statistics executeAll(){
		if(executeNextEvent()==false)
			executeAll();
		return statistics;
	}

	private void updateStatistics(){
		MomentStatistics ms = new MomentStatistics();
		ms.setMachinesUp(SimulationManager.xq.getVirtualMachines().size());
		ms.setMachinesDown(SimulationManager.TOTAL_PHYSICAL_MACHINES-ms.getMachinesUp());
		int vmsUp = 0;
		for(PhysicalMachine pm: SimulationManager.xq.getPhysicalMachines()){
			vmsUp += pm.getVirtualMachines().size();
		}
		ms.setVmsUp(vmsUp);
		ms.setVmsDown(SimulationManager.TOTAL_VMS-ms.getVmsUp());
		statistics.addMomentStats(currentTime, ms);
	}

}
