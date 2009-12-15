package com.magnuson.xen.faulttolerence;

import java.util.List;

public interface Balancer {

	public List<MigrationDecision> calculateAndMigrate();
}
