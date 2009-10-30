package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.*;
import com.magnuson.xen.faulttolerence.simulation.SimulationManager;
import com.magnuson.xen.faulttolerence.simulation.Timeline;

public class MachineUpEvent extends Event {

	private PhysicalMachine pm;

	public MachineUpEvent(PhysicalMachine pm){
		this.pm = pm;
	}

	@Override
	public void execute(Timeline t) {
		SimulationManager.xq.addPhysicalMachine(pm);
	}

	public String toString(){
		return "Machine Up Event - "+this.getExecutionTime();
	}

}