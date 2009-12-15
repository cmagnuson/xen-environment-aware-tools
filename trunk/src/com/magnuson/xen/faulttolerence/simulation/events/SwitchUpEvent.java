package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.simulation.SimulationManager;
import com.magnuson.xen.faulttolerence.simulation.Timeline;

public class SwitchUpEvent extends Event {

	private Switch swi;

	public SwitchUpEvent(Switch swi){
		this.swi = swi;
	}

	@Override
	public void execute(Timeline t) {
		swi.setOnline(true);
		long nextSwitchDownTime = (long)(Math.random()*2*(double)SimulationManager.YEARS/((1.0-swi.getMtbf())*SimulationManager.YEARS/SimulationManager.SWITCH_REBOOT_TIME));
		t.addEvent(new SwitchDownEvent(swi), nextSwitchDownTime);
	}

	public String toString(){
		return "Switch Up Event - "+swi+" - "+this.getExecutionTime();
	}

}