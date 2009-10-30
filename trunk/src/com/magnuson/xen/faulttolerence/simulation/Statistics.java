package com.magnuson.xen.faulttolerence.simulation;

import java.util.*;

public class Statistics {

	private Map<Long, MomentStatistics> stats = new HashMap<Long, MomentStatistics>();
	
	public void addMomentStats(long time, MomentStatistics ms){
		stats.put(time, ms);
	}
	
}
