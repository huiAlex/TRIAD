package edu.nd.dronology.services.extensions.areamapping.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.rmi.CORBA.Util;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class DroneRouteAssignment {

	List<RoutePrimitive> droneRoute;
	
	public DroneRouteAssignment() {
		droneRoute = new ArrayList<>();
	}
	
	public void add(RoutePrimitive routePrimitive) {
		droneRoute.add(routePrimitive);
	}
	
	public List<RoutePrimitive> get(){
		return Collections.unmodifiableList(droneRoute);
	}
	
	public RoutePrimitive get(int entry) {
		return droneRoute.get(entry);
	}
	
	public void set(int index, RoutePrimitive entry) {
		droneRoute.set(index, entry);
	}
	
	public void setDroneRouteAssignment(List<RoutePrimitive> assignment) {
		droneRoute = assignment;
	}
}
