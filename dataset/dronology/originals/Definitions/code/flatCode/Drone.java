package edu.nd.dronology.services.extensions.areamapping.metrics;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Get;
import org.apache.logging.log4j.core.appender.routing.Route;

import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;

public class Drone {
	private Point2D.Double startPoint;
	private Point2D.Double homeLocation;
	private DroneRouteAssignment routeAssignment;
	private String UAVId;
	
	public Drone() {
		startPoint = new Point2D.Double();
		homeLocation = new Point2D.Double();
		routeAssignment = new DroneRouteAssignment();
	}
	
	public Point2D.Double getDroneStartPoint(){
		return startPoint;
	}
	
	public Point2D.Double getDroneHomeLocation(){
		return homeLocation;
	}
	
	public DroneRouteAssignment getDroneRouteAssignment() {
		return routeAssignment;
	}
	
	public List<Point2D.Double> getDroneFullRoute() {
		List<Point2D.Double> fullRoute = new ArrayList<>();
		for(RoutePrimitive route : routeAssignment.get()) {
			fullRoute.addAll(route.getRoute());
		}
		return fullRoute;
	}
	
	public void setDroneStartPoint(Point2D.Double start) {
		startPoint = start;
	}
	
	public void setDroneHomeLocation(Point2D.Double home) {
		homeLocation = home;
	}
	
	public void setDroneRouteAssignment(DroneRouteAssignment route) {
		routeAssignment = route;
	}

	public String getUAVId() {
		return UAVId;
	}

	public void setUAVId(String UAVId) {
		this.UAVId = UAVId;
	}
}
