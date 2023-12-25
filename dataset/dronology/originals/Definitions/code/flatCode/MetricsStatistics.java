package edu.nd.dronology.services.core.areamapping;

import java.util.Collections;
import java.util.List;

public class MetricsStatistics {
	private double equalityOfTasks;
	private double allocationCoverage;
	private double downstreamRatio;
	private boolean batteryFailed;
	private int collisions;
	private double allocationScore;
	private double totalDistance;
	private List<Double> droneDistances;
	private double allocationPriorityCoverage;
	
	public MetricsStatistics(double equalityOfTasks, double allocationCoverage, double downstreamRatio, boolean batteryFailed, int collisions, double totalDistance, 
			List<Double> droneDistances, double priorityCoverage) {
		this.equalityOfTasks = equalityOfTasks;
		this.allocationCoverage = allocationCoverage;
		this.downstreamRatio = downstreamRatio;
		this.batteryFailed = batteryFailed;
		this.collisions = collisions;
		this.totalDistance = totalDistance;
		this.droneDistances = droneDistances;
		this.allocationPriorityCoverage = priorityCoverage;
		calculateAllocationScore();
	}
	
	//score of all metrics combined here
	private void calculateAllocationScore() {
		if(batteryFailed) {
			allocationScore = 0;
		} else {
			allocationScore = 0.25*(equalityOfTasks + allocationCoverage + downstreamRatio - collisions / 5);
		}
	}
	
	public double getEqualityOfTasks() {
		return equalityOfTasks;
	}
	
	public double getAllocationCoverage() {
		return allocationCoverage;
	}
	
	public double getDownstreamToUpstreamRatio() {
		return downstreamRatio;
	}
	
	public boolean getBatteryFailed() {
		return batteryFailed;
	}
	
	public int getCollisions() {
		return collisions;
	}
	
	public double getAllocationScore() {
		return allocationScore;
	}
	
	public double getTotalDistance() {
		return totalDistance;
	}
	
	public List<Double> getDroneDistances(){
		return Collections.unmodifiableList(droneDistances);
	}
	
	public double getAllocationPriorityCoverage() {
		return allocationPriorityCoverage;
	}
}
