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
				t.addEvent(new VmUpEvent(pm, vm), (long)(2.0*Math.random()*SimulationManager.AVG_VIRTUAL_REBOOT_TIME));
			}
			else{
				//move to random available physical machine
				int physicalMachine = (int)(Math.floor(Math.random()*SimulationManager.xq.getPhysicalMachines().size()));
				SimulationManager.xq.addVirtualMachine(vm, SimulationManager.xq.getPhysicalMachines().get(physicalMachine));
			}
		}
	}

	public String toString(){
		return "VM Up Event - "+vm+" - "+this.getExecutionTime();
	}

}
