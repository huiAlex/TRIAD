package edu.nd.dronology.services.extensions.areamapping.metrics;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.services.core.areamapping.MetricsStatistics;

public class AllocationInformation implements Comparable<AllocationInformation> {
	private List<Drone> droneAllocations;
	private MetricsStatistics metricsStatistics;

	public AllocationInformation() {
		droneAllocations = new ArrayList<>();
	}

	public List<Drone> getDroneAllocations() {
		return droneAllocations;
	}

	public MetricsStatistics getMetricStatistics() {
		return metricsStatistics;
	}

	public void setDroneAllocations(List<Drone> droneAllocations) {
		this.droneAllocations = droneAllocations;
	}

	public void setMetricsStatistics(MetricsStatistics metricsStatistics) {
		this.metricsStatistics = metricsStatistics;
	}

	@Override
	public int compareTo(AllocationInformation otherAllocation) {
		if (this.getMetricStatistics().getAllocationScore() < otherAllocation.getMetricStatistics().getAllocationScore()) {
			return 1;
		} else if (otherAllocation.getMetricStatistics().getAllocationScore() < this.getMetricStatistics()
				.getAllocationScore()) {
			return -1;
		}
		return 0;
	}
}
