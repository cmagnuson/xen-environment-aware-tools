package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.*;
import com.magnuson.xen.*;

public class SwitchDownEvent extends Event {

	private Switch swi;
	
	public SwitchDownEvent(Switch swi){
		this.swi = swi;
	}
	
	@Override
	public void execute(Timeline t) {

		swi.setOnline(false);
		t.addEvent(new SwitchUpEvent(swi), SimulationManager.SWITCH_REBOOT_TIME);
	}

	public String toString(){
		return "Switch Down Event - "+this.getExecutionTime();
	}

}
