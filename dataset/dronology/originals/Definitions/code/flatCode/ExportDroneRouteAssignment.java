package edu.nd.dronology.services.core.unused;
//package edu.nd.dronology.services.core.areamapping;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import edu.nd.dronology.services.core.items.FlightRoute;
//import edu.nd.dronology.services.core.items.IFlightRoute;
//
//public class ExportDroneRouteAssignment implements Serializable {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -942703933531286156L;
//	List<IFlightRoute> droneRoute;
//	
//	public ExportDroneRouteAssignment() {
//		droneRoute = new ArrayList<>();
//	}
//	
//	public void add(IFlightRoute flightRoute) {
//		droneRoute.add(flightRoute);
//	}
//	
//	public List<IFlightRoute> get(){
//		return Collections.unmodifiableList(droneRoute);
//	}
//	
//	public IFlightRoute get(int entry) {
//		return droneRoute.get(entry);
//	}
//	
//	public void set(int index, FlightRoute entry) {
//		droneRoute.set(index, entry);
//	}
//	
//	public void setDroneRouteAssignment(List<IFlightRoute> assignment) {
//		droneRoute = assignment;
//	}
//}
