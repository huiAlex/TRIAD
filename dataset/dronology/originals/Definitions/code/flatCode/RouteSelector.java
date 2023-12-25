package edu.nd.dronology.services.extensions.areamapping.unused;
//package edu.nd.dronology.services.extensions.areamapping;
//
//import java.awt.geom.Path2D;
//import java.awt.geom.Point2D;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import edu.nd.dronology.core.coordinate.LlaCoordinate;
//import edu.nd.dronology.core.vehicle.IUAVProxy;
//import edu.nd.dronology.services.core.areamapping.MetricsStatistics;
//import edu.nd.dronology.services.extensions.areamapping.internal.Geometry;
//import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;
//import edu.nd.dronology.services.extensions.areamapping.metrics.AllocationInformation;
//import edu.nd.dronology.services.extensions.areamapping.metrics.Drone;
//import edu.nd.dronology.services.extensions.areamapping.metrics.MetricsRunner;
//import edu.nd.dronology.services.extensions.areamapping.metrics.MetricsUtilities;
//import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
//import edu.nd.dronology.services.extensions.areamapping.util.Utilities;
//
//public class RouteSelector  {
//
//	private static final double APERATURE_WIDTH = 10;
//	private static final double APERATURE_HEIGHT= 0.8* APERATURE_WIDTH;
//	private static final double OVERLAP_FACTOR = 0.7;
//	private double avgLatitude;
//	private int availableDrones;
//	private List<RoutePrimitive> routePrimitives;
//	private MetricsRunner metricsRunner;
//	private List<IUAVProxy> uavs;
//
//	
//	public void initialize(List<RoutePrimitive>routePrimitives, List<RiverBank> bankList, Path2D.Double totalRiverSegment, int availableDrones, double avgLatitude, Collection<IUAVProxy> uavs) {
//		// TODO Auto-generated method stub
//		routePrimitives = Utilities.splitRoutePrimitives(routePrimitives, 4, APERATURE_HEIGHT, OVERLAP_FACTOR);
//		this.routePrimitives = routePrimitives;
//		this.availableDrones = availableDrones;
//		this.avgLatitude = avgLatitude;
//		this.uavs = new ArrayList<>(uavs);
//		metricsRunner = new MetricsRunner(routePrimitives, totalRiverSegment, bankList, APERATURE_WIDTH, APERATURE_HEIGHT, uavs.size());
//	}
//	
//	//GeneratedRouteAssignment....
//	private List<Drone> generateRandomAssingments(){
//		Set<Integer> assignedRoutes = new HashSet<>();
//		availableDrones = uavs.size();
//		int droneNum;
//		int routeNum;
//	
//		int routeAssignmentNum = MetricsUtilities.generateRandomNumber(routePrimitives.size()-1, 1);
//		List<Drone> droneList = new ArrayList<>();
//		for(int i = 0; i < availableDrones; i++) {
//			droneList.add(new Drone());
//			LlaCoordinate home = uavs.get(i).getHomeLocation();
//			LlaCoordinate currentLocation = uavs.get(i).getCoordinates();
//			droneList.get(i).setDroneHomeLocation(Geometry.gpsToCartesian(new Point2D.Double(home.getLatitude(),home.getLongitude()), avgLatitude));
//			droneList.get(i).setDroneStartPoint(Geometry.gpsToCartesian(new Point2D.Double(currentLocation.getLatitude(),currentLocation.getLongitude()), avgLatitude));
//			droneList.get(i).setUAVId(uavs.get(i).getID());
//		}
//		while(assignedRoutes.size() < routeAssignmentNum) {
//			//assign drone routes in here
//			droneNum = MetricsUtilities.generateRandomNumber(availableDrones-1,0);
//			routeNum = MetricsUtilities.generateRandomNumber(routeAssignmentNum, 0);
//			while(assignedRoutes.contains(routeNum)) {
//				routeNum = MetricsUtilities.generateRandomNumber(routeAssignmentNum, 0);
//			}
//			droneList.get(droneNum).getDroneRouteAssignment().add(routePrimitives.get(routeNum));
//			assignedRoutes.add(routeNum);
//		}
//		return droneList;
//	}
//	
//	private MetricsStatistics generateMetricsStatistics(List<Drone> drones) {
//		metricsRunner.setDroneAssignments(drones);
//		return metricsRunner.runMetrics();
//	}
//	
//	//use a loop to pick best route
//	//return wrapper for list<drone> and metrics
//	public List<AllocationInformation> generateAssignments() {
//		// loop to create assignments and check for best assignment
//		List<AllocationInformation> allAllocations = new ArrayList<>();
//		AllocationInformation finalAllocation = new AllocationInformation();
//		/*List<Drone> assignment = generateRandomAssingments();
//		finalAllocation.setDroneAllocations(assignment);
//		finalAllocation.setMetricsStatistics(generateMetricsStatistics(assignment));
//		allAllocations.add(finalAllocation);*/
//		for(int i = 0; i < 100; i++) {
//			List<Drone> assignments = generateRandomAssingments();
//			AllocationInformation currentAllocation = new AllocationInformation();
//			currentAllocation.setDroneAllocations(assignments);
//			currentAllocation.setMetricsStatistics(generateMetricsStatistics(assignments));
//			allAllocations.add(currentAllocation);
//			/*boolean added = false;
//			for(int j = 0; j < top5Allocations.size(); j++) {
//				//allows 0 collisions
//				if(top5Allocations.get(j).getMetricStatistics().getAllocationScore() < currentAllocation.getMetricStatistics().getAllocationScore() && currentAllocation.getMetricStatistics().getCollisions() == 0) {
//					top5Allocations.add(j, currentAllocation);
//					added = true;
//					if(top5Allocations.size() > 5) {
//						top5Allocations.remove(top5Allocations.size()-1);
//					}
//					break;
//				}
//			}
//			//allows 0 collisions
//			if(top5Allocations.size() < 5 && !added && currentAllocation.getMetricStatistics().getCollisions() == 0) {
//				top5Allocations.add(currentAllocation);
//			}*/
//			
//		}
//		Collections.sort(allAllocations);
//	/*	for(AllocationInformation entry : top5Allocations) {
//			List<Drone> routeAssignments = new ArrayList<>();
//			routeAssignments = entry.getDroneAllocations();
//			for(Drone drone : routeAssignments) {
//				drone.cartesianToGps(avgLatitude);
//			}
//			entry.setDroneAllocations(routeAssignments);
//		}*/
//		for(RoutePrimitive route : routePrimitives) {
//			route = Utilities.cartesianRouteToGpsRoute(route, avgLatitude);
//		}
//		//are home and start locations converted back?
//		return allAllocations;
//	}
//	
//
//}
