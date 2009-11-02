package com.magnuson.xen.faulttolerence.simulation.events;

import com.magnuson.xen.faulttolerence.simulation.*;

public abstract class Event implements Comparable<Event> {

	private long executionTime = 0;
	
	public void setExecutionTime(long time){
		executionTime = time;
	}
	public long getExecutionTime(){
		return executionTime;
	}

	public int compareTo(Event e){
		long test = executionTime-e.getExecutionTime();
		if(test>0){
			return 1;
		}
		else if(test<0){
			return -1;
		}
		return 0;
	}

	public abstract void execute(Timeline t);
}
