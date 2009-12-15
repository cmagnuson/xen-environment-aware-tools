package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.SimulationManager;
import com.magnuson.xen.faulttolerence.simulation.Timeline;
import com.magnuson.xen.faulttolerence.*;

public class BalancingEvent extends Event {

	
	private Balancer balancer;
	
	public BalancingEvent(Balancer b){
		this.balancer = b;
	}

	@Override
	public void execute(Timeline t) {

		balancer.calculateAndMigrate();
		scheduleNext(t);
	}

	public String toString(){
		return "Balancing Event - "+this.getExecutionTime();
	}

	public boolean equals(Object o){
		if(o instanceof BalancingEvent){
			return ((BalancingEvent)o).getExecutionTime()==this.getExecutionTime();
		}
		return false;
	}

	public void scheduleNext(Timeline t){
		//schedule periodic balancing
		long nextBalancing;
		if(SimulationManager.faultManagingEnabled){
			nextBalancing = SimulationManager.AUTOMATIC_MIGRATE_POLL_RATE; 
		}
		else{
			nextBalancing = SimulationManager.MANUAL_MIGRATE_POLL_RATE; 
		}

		t.addEvent(new BalancingEvent(balancer), nextBalancing);
	}

}
