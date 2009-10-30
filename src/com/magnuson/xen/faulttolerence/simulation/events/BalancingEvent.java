package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.SimulationManager;
import com.magnuson.xen.faulttolerence.simulation.Timeline;

public class BalancingEvent extends Event {

	@Override
	public void execute(Timeline t) {
		SimulationManager.balancer.calculateAndMigrate();
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
}
