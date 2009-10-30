package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.*;

public abstract class Event {

	private long executionTime = 0;
	public void setExecutionTime(long time){
		executionTime = time;
	}
	public long getExecutionTime(){
		return executionTime;
	}
	
	public int compareTo(Event e){
		return (int)(executionTime-e.getExecutionTime());
	}
	
	public abstract void execute(Timeline t);
}
