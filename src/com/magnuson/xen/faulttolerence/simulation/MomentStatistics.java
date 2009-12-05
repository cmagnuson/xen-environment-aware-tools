package com.magnuson.xen.faulttolerence.simulation;

import com.magnuson.xen.faulttolerence.simulation.events.*;

public class MomentStatistics {

	private int vmsUp;
	private int vmsDown;
	private int machinesUp;
	private int machinesDown;
	private Event event;

	public MomentStatistics(Event e){
		super();
		event = e;
		vmsUp = 0;
		vmsDown = 0;
		machinesUp = 0;
		machinesDown = 0;
	}
	
	public MomentStatistics(int vmsUp, int vmsDown, int machinesUp,
			int machinesDown) {
		super();
		this.vmsUp = vmsUp;
		this.vmsDown = vmsDown;
		this.machinesUp = machinesUp;
		this.machinesDown = machinesDown;
	}
		
	public int getVmsUp() {
		return vmsUp;
	}
	public void setVmsUp(int vmsUp) {
		this.vmsUp = vmsUp;
	}
	public int getVmsDown() {
		return vmsDown;
	}
	public void setVmsDown(int vmsDown) {
		this.vmsDown = vmsDown;
	}
	public int getMachinesUp() {
		return machinesUp;
	}
	public void setMachinesUp(int machinesUp) {
		this.machinesUp = machinesUp;
	}
	public int getMachinesDown() {
		return machinesDown;
	}
	public void setMachinesDown(int machinesDown) {
		this.machinesDown = machinesDown;
	}
	public Event getEvent(){
		return event;
	}
	
}
