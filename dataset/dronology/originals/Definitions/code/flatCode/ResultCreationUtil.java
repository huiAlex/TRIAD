package edu.nd.dronology.services.extensions.areamapping.selection;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.areamapping.ExportAllocationInformation;
import edu.nd.dronology.services.core.areamapping.ExportDrone;
import edu.nd.dronology.services.core.items.FlightRoute;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.extensions.areamapping.internal.Geometry;
import edu.nd.dronology.services.extensions.areamapping.metrics.AllocationInformation;
import edu.nd.dronology.services.extensions.areamapping.metrics.Drone;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive.RouteType;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class ResultCreationUtil {

	private static final double ALTITUDE = 20;
	private List<RoutePrimitive> routePrimitives;
	private Map<RouteType, AtomicInteger> typeCounter = new HashMap<>();
	private Map<RoutePrimitive, IFlightRoute> routeMap = new HashMap<>();
	private double avgLatitude;

	public ResultCreationUtil(List<RoutePrimitive> routePrimitives) {
		this.routePrimitives = routePrimitives;
	}

	public void createRoutes(double avgLatitude) {
		this.avgLatitude = avgLatitude;
		for (RoutePrimitive pr : routePrimitives) {
			int val = 1;
			pr = Utilities.cartesianRouteToGpsRoute(pr, avgLatitude);
			RouteType type = pr.getRouteType();
			if (typeCounter.containsKey(type)) {
				val = typeCounter.get(type).incrementAndGet();
			} else {
				typeCounter.put(type, new AtomicInteger(1));
			}
			makeFlightRoute(pr, ALTITUDE, type.toString() + "_" + val);

		}

	}

	// edit to return IFlightRoute with name as something with type of route
	public IFlightRoute makeFlightRoute(RoutePrimitive route, double altitude, String name) {
		IFlightRoute newRoute = new FlightRoute();
		for (Point2D.Double entry : route.getRoute()) {
			newRoute.addWaypoint(new Waypoint(new LlaCoordinate(entry.getX(), entry.getY(), altitude)));
		}
		newRoute.setName(name);
		routeMap.put(route, newRoute);
		return newRoute;
	}

	public RouteSelectionResult createResult(List<AllocationInformation> allAllocations) {
		RouteSelectionResult result = new RouteSelectionResult();
		// List<RoutePrimitive> exportedRouteAssignments = new ArrayList<>();
		for (AllocationInformation entry : allAllocations) {
			ExportAllocationInformation ass = new ExportAllocationInformation();
			ass.setMetricsStatistics(entry.getMetricStatistics());
			result.add(ass);
			for (Drone dr : entry.getDroneAllocations()) {
				ExportDrone exp = new ExportDrone(dr.getUAVId());
				java.awt.geom.Point2D.Double home = Geometry.cartesianToGPS(dr.getDroneHomeLocation(), avgLatitude);
				java.awt.geom.Point2D.Double start = Geometry.cartesianToGPS(dr.getDroneStartPoint(), avgLatitude);

				exp.setDroneHomeLocation(new LlaCoordinate(home.getX(), home.getY(), ALTITUDE));
				exp.setDroneStartPoint(new LlaCoordinate(start.getX(), start.getY(), ALTITUDE));

				dr.getDroneRouteAssignment().get().forEach(pr -> {
					exp.addRoute(routeMap.get(pr));
				});
				ass.addDroneAllocation(exp);
			}

		}
		// result.setExportAllocationInformation(info);
		return result;
	}

}
