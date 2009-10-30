package com.magnuson.xen.faulttolerence.simulation;

import java.util.*;

public class Statistics {

	private Map<Long, MomentStatistics> stats = new HashMap<Long, MomentStatistics>();
	
	public void addMomentStats(long time, MomentStatistics ms){
		stats.put(time, ms);
	}
	
	public String getCsv(){
		String header = "time,VM_UP,VM_DOWN,PM_UP,PM_DOWN\n";
		String body = "";
		for(long time: stats.keySet()){
			MomentStatistics ms = stats.get(time);
			body+=time+","+ms.getVmsUp()+","+ms.getVmsDown()+","+ms.getMachinesUp()+","+ms.getMachinesDown()+"\n";
		}
		return header+body;
	}
	
}
