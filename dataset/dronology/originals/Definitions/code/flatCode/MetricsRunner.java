package edu.nd.dronology.services.extensions.areamapping.metrics;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.io.path.Path;

import edu.nd.dronology.services.core.areamapping.MetricsStatistics;
import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class MetricsRunner {
	private List<Drone> drones;
	private List<RoutePrimitive> allRoutes;
	private Path2D.Double totalRiverSegment;
	private List<RiverBank> bankList;
	private double APERATURE_WIDTH;
	private double APERATURE_HEIGHT;
	
	public MetricsRunner(List<RoutePrimitive> routes, Path2D.Double riverSegment, List<RiverBank> listOfBanks, double A_W, double A_H, int uavs) {
		drones = new ArrayList<>();
		for(int i = 0; i < uavs; i++) {
			drones.add(new Drone());
		}
		allRoutes = routes;
		totalRiverSegment = riverSegment;
		bankList = listOfBanks;
		APERATURE_WIDTH = A_W;
		APERATURE_HEIGHT = A_H;
		//droneSetup();
	}
	
	public void droneSetup() {
		int counter = 0;
		for(Drone drone : drones) {
			DroneRouteAssignment routeAssignment = new DroneRouteAssignment();
			routeAssignment.add(allRoutes.get(counter));
			counter += 1;
			drone.setDroneRouteAssignment(routeAssignment);
		}
		drones.get(0).setDroneStartPoint(new Point2D.Double(4639658.290263815, -7163020.664734639));
		drones.get(0).setDroneHomeLocation(new Point2D.Double(4639658.290263815, -7163020.664734639));
		drones.get(1).setDroneStartPoint(new Point2D.Double(4639698.333003777, -7163031.375602997));
		drones.get(1).setDroneHomeLocation(new Point2D.Double(4639698.333003777, -7163031.375602997));
		drones.get(2).setDroneStartPoint(new Point2D.Double(4639719.355442258, -7163044.2452510195));
		drones.get(2).setDroneHomeLocation(new Point2D.Double(4639719.355442258, -7163044.2452510195));
		drones.get(3).setDroneStartPoint(new Point2D.Double(4639658.290263815, -7163020.664734639));
		drones.get(3).setDroneHomeLocation(new Point2D.Double(4639658.290263815, -7163020.664734639));
	}
	
	public void setDroneAssignments(List<Drone> drones) {
		this.drones = drones;
	}
	
	public MetricsStatistics runMetrics() {
		double equalityOfTasks =  MetricsUtilities.equalityOfTasks(drones);

		double allocationCoverage =  MetricsUtilities.calculateRouteCoverage(drones, totalRiverSegment, bankList, APERATURE_WIDTH, APERATURE_HEIGHT);

		double downstreamRatio =  MetricsUtilities.calculateDownstreamRatio(drones);

		boolean batteryFailed = false;
		for(Drone drone : drones) {
			if(MetricsUtilities.batteryFailure(drone)) {
				batteryFailed = true;
				break;
			}
		}

		for(Drone drone : drones) {
			if(MetricsUtilities.routeAssignmentOutsideRiver(drone.getDroneRouteAssignment(), totalRiverSegment, APERATURE_HEIGHT)) {
				System.out.println("route transition goes outside river segment");
			}
		}

		int collisions = MetricsUtilities.collisionCheck(drones);

		double totalDistance = MetricsUtilities.totalAllocationDistance(drones);
		
		List<Double> droneDistances = MetricsUtilities.calculateDroneDistances(drones);
		
		double priorityCoverage = MetricsUtilities.calculateAllocationPriorityCoverage(drones);
	
		return new MetricsStatistics(equalityOfTasks, allocationCoverage, downstreamRatio, batteryFailed, collisions, totalDistance, droneDistances, priorityCoverage);
	}
}
