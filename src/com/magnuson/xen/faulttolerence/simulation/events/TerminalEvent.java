package com.magnuson.xen.faulttolerence.simulation.events;
import com.magnuson.xen.faulttolerence.simulation.Timeline;


public class TerminalEvent extends Event {

	@Override
	public void execute(Timeline t) {
		//no action required
	}

	public String toString(){
		return "Terminal Event - "+this.getExecutionTime();
	}
}
