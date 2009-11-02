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
	
		if(SimulationManager.xq.getPhysicalMachines().size()<2){
			long nextMachineDownTime = (long)(Math.random()*2*SimulationManager.MEAN_HARDWARE_UPTIME);
			t.addEvent(new MachineDownEvent(), nextMachineDownTime);
		}
	}

	public String toString(){
		return "Machine Up Event - "+pm+" - "+this.getExecutionTime();
	}

}