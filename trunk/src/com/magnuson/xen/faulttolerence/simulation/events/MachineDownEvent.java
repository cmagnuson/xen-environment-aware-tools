package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.*;
import com.magnuson.xen.*;

public class MachineDownEvent extends Event {

	private PhysicalMachine failedMachine;
	
	public MachineDownEvent(PhysicalMachine pm){
		failedMachine = pm;
	}
	
	@Override
	public void execute(Timeline t) {

		
		SimulationManager.xq.removePhysicalMachine(failedMachine);
		//schedule vm up plan - spin up on any available machine
		//time based off of manual or not
		while(failedMachine.getVirtualMachines().size()>0){
			VirtualMachine vm = failedMachine.getVirtualMachines().get(0);
			t.addEvent(new VmUpEvent(failedMachine,vm), (long)(Math.random()*2*SimulationManager.AVG_VIRTUAL_REBOOT_TIME));
			SimulationManager.xq.removeVirtualMachine(vm, failedMachine);
		}
		//schedule machine up plan
		t.addEvent(new MachineUpEvent(failedMachine), SimulationManager.PHYSICAL_REBOOT_TIME);
		
//		//bring down a machine if there is one available
//		if(SimulationManager.xq.getPhysicalMachines().size()>0){
//			//pick machine at random and remove
//			int failedMachineIndex = (int)(Math.floor(SimulationManager.xq.getPhysicalMachines().size()*Math.random()));
//			PhysicalMachine failedMachine = SimulationManager.xq.getPhysicalMachines().get(failedMachineIndex);
//			SimulationManager.xq.removePhysicalMachine(failedMachine);
//
//			//schedule vm up plan - spin up on any available machine
//			//time based off of manual or not
//			while(failedMachine.getVirtualMachines().size()>0){
//				VirtualMachine vm = failedMachine.getVirtualMachines().get(0);
//				t.addEvent(new VmUpEvent(failedMachine,vm), (long)(Math.random()*(double)SimulationManager.AVG_VIRTUAL_REBOOT_TIME*2));
//				failedMachine.removeVirtualMachine(vm);
//			}
//
//			//schedule machine up plan
//			t.addEvent(new MachineUpEvent(failedMachine), SimulationManager.PHYSICAL_REBOOT_TIME);
//		}

		//schedule next machine down plan
//		if(SimulationManager.xq.getPhysicalMachines().size()>0){
//			long nextMachineDownTime = (long)(Math.random()*2.0*(double)SimulationManager.MEAN_HARDWARE_UPTIME)/SimulationManager.xq.getPhysicalMachines().size();
//			t.addEvent(new MachineDownEvent(), nextMachineDownTime);
//		}
	}

	public String toString(){
		return "Machine Down Event - "+this.getExecutionTime();
	}

}
