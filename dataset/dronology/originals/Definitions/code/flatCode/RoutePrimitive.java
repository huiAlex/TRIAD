package edu.nd.dronology.services.extensions.areamapping.model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import edu.nd.dronology.services.extensions.areamapping.internal.Geometry;
import edu.nd.dronology.services.extensions.areamapping.internal.ImageWaypoints;

public class RoutePrimitive{

	private List<Point2D.Double> routePrimitive;
	private ImageWaypoints imageWaypoints;
	private boolean downstreamDirection;
	public enum RouteType {
		CRISSCROSS,
		INNER_CRISSCROSS,
		RIVERBANK,
		PRIORITYAREA,
		HOME
	}
	private final RouteType type;
	private int routeWeight;
	
	public RoutePrimitive(RouteType type, int routeWeight) {
		this.type = type;
		this.routeWeight = routeWeight;
		routePrimitive = new Vector<Point2D.Double>();
		imageWaypoints = new ImageWaypoints();
		downstreamDirection = true;
	}
	
	public void addRouteWaypoint(Point2D.Double routeWaypoint) {
		routePrimitive.add(routeWaypoint);
	}
	
	public void reverseRoute() {
		Collections.reverse(routePrimitive);
		if(downstreamDirection) {
			downstreamDirection = false;
		} else {
			downstreamDirection = true;
		}
	}
	
	public boolean getDownstreamDirection() {
		return downstreamDirection;
	}
	
	public List<Point2D.Double> getRoute(){
		return Collections.unmodifiableList(routePrimitive);
	}
	
	public Point2D.Double getRouteStartPoint(){
		return routePrimitive.get(0);
	}
	
	public Point2D.Double getRouteEndPoint(){
		return routePrimitive.get(size()-1);
	}
	
	public double getRouteDistance() {
		return Geometry.routePrimitiveDistance(routePrimitive);
	}
	
	public Point2D.Double getRouteWaypoint(int entry){
		return routePrimitive.get(entry);
	}
	
	public void insertRouteWaypoint(int index, Point2D.Double waypoint) {
		routePrimitive.add(index, waypoint);
	}
	
	public void setRouteWaypoint(int index, Point2D.Double entry) {
		routePrimitive.set(index, entry);
	}
	
	public int size() {
		return routePrimitive.size();
	}
	
	public ImageWaypoints getIWP() {
		return imageWaypoints;
	}
	
	public RouteType getRouteType() {
		return type;
	}
	
	public int getRouteWeight() {
		return routeWeight;
	}
}
