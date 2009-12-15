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

	//not nessisary, just for performance - saves on unneeded balancings
	private boolean balanced = false;

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

		if(e instanceof BalancingEvent){
			if(balanced){
				((BalancingEvent)e).scheduleNext(this);
				return;
			}
			else{
				balanced = true;
			}
		}
		else{
			balanced = false;
		}

		log.debug(eventNum+" Executing: "+e);
		e.execute(this);

		updateStatistics(e);
	}

	public Statistics executeAll(){
		while(timeline.size()>0){
			executeNextEvent();
		}
		return statistics;
	}

	private void updateStatistics(Event e){
		MomentStatistics ms = new MomentStatistics(e);
		ms.setMachinesUp(SimulationManager.xq.getPhysicalMachines().size());
		ms.setMachinesDown(SimulationManager.TOTAL_PHYSICAL_MACHINES-ms.getMachinesUp());
		int vmsUp = 0;
		for(PhysicalMachine pm: SimulationManager.xq.getPhysicalMachines()){
			if(pm.getSwitch().hasInternetConnection()){
				vmsUp += pm.getVirtualMachines().size();
			}
		}
		ms.setVmsUp(vmsUp);
		ms.setVmsDown(SimulationManager.TOTAL_VMS-ms.getVmsUp());
		statistics.addMomentStats(currentTime, ms);
	}

	public long getTime(){
		return currentTime;
	}
}
