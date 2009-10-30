package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.Timeline;

public class MachineDownEvent extends Event {

	@Override
	public void execute(Timeline t) {
		//pick machine at random and remove
		
		//schedule appropriate migration plan
		
		//schedule vm up plan - spin up on any available machine
		//time based off of manual or not
		
		//schedule machine up plan
		
		//scedule next machine down plan
	}

	public String toString(){
		return "Machine Down Event - "+this.getExecutionTime();
	}
	
}
