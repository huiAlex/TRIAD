package edu.nd.dronology.services.core.areamapping;

import java.util.ArrayList;
import java.util.List;

public class ExportAllocationInformation {
	private List<ExportDrone> droneAllocations;
	private MetricsStatistics metricsStatistics;
	
	public ExportAllocationInformation() {
		droneAllocations = new ArrayList<>();
	}
	
	public List<ExportDrone> getDroneAllocations(){
		return droneAllocations;
	}
	
	public MetricsStatistics getMetricStatistics() {
		return metricsStatistics;
	}
	
	public void addDroneAllocation(ExportDrone droneAllocation) {
		droneAllocations.add(droneAllocation);
	}
	
	public void setDroneAllocations(List<ExportDrone> droneAllocations) {
		this.droneAllocations = droneAllocations;
	}
	
	public void setMetricsStatistics(MetricsStatistics metricsStatistics) {
		this.metricsStatistics = metricsStatistics;
	}
}
