package com.magnuson.xen.faulttolerence.simulation;

import java.util.*;
import org.apache.log4j.Logger;

public class Statistics {

	private Map<Long, MomentStatistics> stats = new TreeMap<Long, MomentStatistics>();
	static Logger log = Logger.getLogger(Statistics.class);

	public void addMomentStats(long time, MomentStatistics ms){
		stats.put(time, ms);
	}

	public String getCsv(){
		String header = "time,VM_UP,VM_DOWN,PCT_VM_UP,PM_UP,PM_DOWN,PCT_PM_UP\n";
		String body = "";
		for(long time: stats.keySet()){
			MomentStatistics ms = stats.get(time);
			body+=time+","+ms.getVmsUp()+","+ms.getVmsDown()+","+((double)ms.getVmsUp()/(double)SimulationManager.TOTAL_VMS)+","+ms.getMachinesUp()+","
				+ms.getMachinesDown()+","+((double)ms.getMachinesUp()/(double)SimulationManager.TOTAL_PHYSICAL_MACHINES)+"\n";
		}
		return header+body;
	}

	//TODO: clean this up, doesn't appear to be reporting correct numbers
	public double getAvgVmUptime(){
		long totalTime = SimulationManager.RUNNING_TIME;
		double runningTotal = 0;
		
		long lastTime = 0;
		for(long time: stats.keySet()){
			if(time==0){
				continue;
			}
			double ratio = (time-lastTime)/(double)totalTime;
			log.trace(ratio+" "+lastTime+" "+time);
			runningTotal+=ratio*(stats.get(lastTime).getVmsUp()/SimulationManager.TOTAL_VMS);
			lastTime = time;
		}
		
		return runningTotal;
	}
}
