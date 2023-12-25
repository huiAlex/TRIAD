package edu.nd.dronology.services.core.unused;
//package edu.nd.dronology.services.core.areamapping;
//
//import java.io.Serializable;
//import java.util.Collections;
//import java.util.List;
//import java.util.Vector;
//
//import edu.nd.dronology.core.coordinate.LlaCoordinate;
//
//public class ExportRoutePrimitive implements Serializable{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -7238362877727584258L;
//	private List<LlaCoordinate> routePrimitive;
//	private boolean downstreamDirection;
//	
//	public ExportRoutePrimitive() {
//		routePrimitive = new Vector<LlaCoordinate>();
//		downstreamDirection = true;
//	}
//	
//	public void addRouteWaypoint(LlaCoordinate routeWaypoint) {
//		routePrimitive.add(routeWaypoint);
//	}
//	
//	public void reverseRoute() {
//		Collections.reverse(routePrimitive);
//		if(downstreamDirection) {
//			downstreamDirection = false;
//		} else {
//			downstreamDirection = true;
//		}
//	}
//	
//	public boolean getDownstreamDirection() {
//		return downstreamDirection;
//	}
//	
//	public List<LlaCoordinate> getRoute(){
//		return Collections.unmodifiableList(routePrimitive);
//	}
//	
//	public LlaCoordinate getRouteStartPoint(){
//		return routePrimitive.get(0);
//	}
//	
//	public LlaCoordinate getRouteEndPoint(){
//		return routePrimitive.get(size()-1);
//	}
//	
//	public LlaCoordinate getRouteWaypoint(int entry){
//		return routePrimitive.get(entry);
//	}
//	
//	public void insertRouteWaypoint(int index, LlaCoordinate waypoint) {
//		routePrimitive.add(index, waypoint);
//	}
//	
//	public void setRouteWaypoint(int index, LlaCoordinate entry) {
//		routePrimitive.set(index, entry);
//	}
//	
//	public void setDownstreamDirection(boolean downstream) {
//		downstreamDirection = downstream;
//	}
//	
//	public int size() {
//		return routePrimitive.size();
//	}
//}
