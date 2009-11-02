package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.*;
import com.magnuson.xen.*;

public class VmUpEvent extends Event {

	private PhysicalMachine pm;
	private VirtualMachine vm;

	public VmUpEvent(PhysicalMachine pm, VirtualMachine vm){
		this.pm = pm;
		this.vm = vm;
	}

	@Override
	public void execute(Timeline t) {
		//it is on an active machine, must have been rebooted between when this was fired and now
		if(SimulationManager.xq.getPhysicalMachines().contains(pm)){
			SimulationManager.xq.addVirtualMachine(vm, pm);
			return;
		}

		//this machine is still down, move VM elsewhere
		else{
			if(SimulationManager.xq.getPhysicalMachines().size()<1){
				//no available physical machines, wait for one to be up
				t.addEvent(new VmUpEvent(pm, vm), (long)(2.0*Math.random()*(double)SimulationManager.AVG_VIRTUAL_REBOOT_TIME));
			}
			else{
				//move to first available physical machine
				SimulationManager.xq.migrateVirtualMachine(vm, pm, SimulationManager.xq.getPhysicalMachines().get(0));
			}
		}
	}

	public String toString(){
		return "VM Up Event - "+vm+" - "+this.getExecutionTime();
	}

}
