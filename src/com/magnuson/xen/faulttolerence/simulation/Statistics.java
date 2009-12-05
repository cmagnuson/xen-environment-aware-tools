package com.magnuson.xen.faulttolerence.simulation;

import java.util.*;
import org.apache.log4j.Logger;

public class Statistics {

	private TreeMap<Long, MomentStatistics> stats = new TreeMap<Long, MomentStatistics>();
	static Logger log = Logger.getLogger(Statistics.class);
	
	public void addMomentStats(long time, MomentStatistics ms){
		stats.put(time, ms);
	}

	public String getCsv(){
		String header = "TIME,VM_UP,VM_DOWN,PCT_VM_UP,PM_UP,PM_DOWN,PCT_PM_UP,EVENT\n";
		String body = "";
		for(long time: stats.keySet()){
			MomentStatistics ms = stats.get(time);
			body+=time+","+ms.getVmsUp()+","+ms.getVmsDown()+","+((double)ms.getVmsUp()/(double)SimulationManager.TOTAL_VMS)+","+ms.getMachinesUp()+","
				+ms.getMachinesDown()+","+((double)ms.getMachinesUp()/(double)SimulationManager.TOTAL_PHYSICAL_MACHINES)+","+ms.getEvent()+"\n";
		}
		return header+body;
	}

	public double getAvgVmUptime(){
		long totalTime = stats.lastKey();
		double runningTotal = 0;
		
		for(long time: stats.keySet()){
			if(time==0){
				continue;
			}
			
			double ratio = (time-stats.lowerKey(time))/(double)totalTime;
			log.trace(ratio+" "+stats.lowerKey(time)+" "+time);
			
			MomentStatistics lower = stats.lowerEntry(time).getValue();
			runningTotal+=ratio*((double)lower.getVmsUp()/(double)(lower.getVmsDown()+lower.getVmsUp()));
		}
		
		return runningTotal;
	}
	
	
	public double getAvgServiceUptime(){
		int minVmsUp = (int)Math.ceil((double)SimulationManager.TOTAL_VMS * SimulationManager.SERVICE_UP_PCT);
		
		long totalTime = stats.lastKey();
		double runningTotal = 0;
		
		for(long time: stats.keySet()){
			if(time==0){
				continue;
			}
			double ratio = (double)(time-stats.lowerKey(time))/(double)totalTime;
			log.trace(ratio+" "+stats.lowerKey(time)+" "+time);
			
			MomentStatistics lower = stats.lowerEntry(time).getValue();
			if(lower.getVmsUp()<minVmsUp){
			}
			else{
				runningTotal+=ratio;
			}
		}
		
		return runningTotal;
	}
}
