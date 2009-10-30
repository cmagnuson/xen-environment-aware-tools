package com.magnuson.xen.faulttolerence.simulation.events;

import org.apache.log4j.Logger;
import com.magnuson.xen.faulttolerence.simulation.Timeline;

public class TerminalEvent extends Event {

	static Logger log = Logger.getLogger(TerminalEvent.class);

	@Override
	public void execute(Timeline t) {
		log.error("TerminalEvent Execute Called, THIS SHOULD NEVER HAPPEN!");
		return;
	}

	public String toString(){
		return "Terminal Event - "+this.getExecutionTime();
	}
}
