package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.*;
import com.magnuson.xen.*;

public class MachineDownEvent extends Event {

	@Override
	public void execute(Timeline t) {
		//pick machine at random and remove
		int failedMachineIndex = (int)(Math.floor(SimulationManager.xq.getPhysicalMachines().size()*Math.random()));
		PhysicalMachine failedMachine = SimulationManager.xq.getPhysicalMachines().get(failedMachineIndex);
		SimulationManager.xq.removePhysicalMachine(failedMachine);
		
		//schedule periodic balancing
		long nextBalancing = 0;
		if(SimulationManager.faultManagingEnabled){
			nextBalancing = ((long)Math.ceil(this.getExecutionTime()+1/SimulationManager.AUTOMATIC_MIGRATE_POLL_RATE))*SimulationManager.AUTOMATIC_MIGRATE_POLL_RATE;
		}
		else{
			nextBalancing = ((long)Math.ceil(this.getExecutionTime()+1/SimulationManager.MANUAL_MIGRATE_POLL_RATE))*SimulationManager.MANUAL_MIGRATE_POLL_RATE;
		}
		long nextBalancingTime = nextBalancing-this.getExecutionTime();
		
		t.addEvent(new BalancingEvent(), nextBalancingTime);
		
		//schedule vm up plan - spin up on any available machine
		//time based off of manual or not
		while(failedMachine.getVirtualMachines().size()>0){
			VirtualMachine vm = failedMachine.getVirtualMachines().get(0);
			t.addEvent(new VmUpEvent(failedMachine,vm), SimulationManager.VIRTUAL_REBOOT_TIME);
			failedMachine.removeVirtualMachine(vm);
		}
		
		//schedule machine up plan
		t.addEvent(new MachineUpEvent(failedMachine), SimulationManager.PHYSICAL_REBOOT_TIME);
		
		//schedule next machine down plan
		long nextMachineDownTime = (long)(Math.random()*2*SimulationManager.MEAN_HARDWARE_UPTIME);
		t.addEvent(new MachineDownEvent(), nextMachineDownTime);
	}

	public String toString(){
		return "Machine Down Event - "+this.getExecutionTime();
	}
	
}
