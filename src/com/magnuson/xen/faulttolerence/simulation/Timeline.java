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

	static int eventNum = 0;
	
	public Timeline(){}

	public void addEvent(Event e, long scheduledOffset){
		e.setExecutionTime(currentTime+scheduledOffset);
		if(timeline.contains(e)){
			log.trace("Duplicate event added: "+e);
			return;
		}
		if(e.getExecutionTime()>SimulationManager.RUNNING_TIME){
			log.trace("Event added after end of the world: "+e);
			return;
		}
		else{
			log.trace("Adding event to timeline: "+e);
			timeline.add(e);
		}
	}

	private void executeNextEvent(){		
		Event e = timeline.poll();
		currentTime = e.getExecutionTime();

		eventNum++;
		log.debug(eventNum+" Executing: "+e);
		e.execute(this);
		
		updateStatistics();
	}

	public Statistics executeAll(){
		while(timeline.size()>0){
			executeNextEvent();
		}
		return statistics;
	}

	private void updateStatistics(){
		MomentStatistics ms = new MomentStatistics();
		ms.setMachinesUp(SimulationManager.xq.getPhysicalMachines().size());
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
