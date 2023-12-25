package edu.nd.dronology.services.core.areamapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.services.core.items.IFlightRoute;

public class ExportDrone implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6744520905964759937L;
	private LlaCoordinate startPoint;
	private LlaCoordinate homeLocation;
	private List<IFlightRoute> routeAssignment;
	private final String UAVId;

	public ExportDrone(String UAVId) {
		this.UAVId = UAVId;
		routeAssignment = new ArrayList<>();
	}

	public LlaCoordinate getDroneStartPoint() {
		return startPoint;
	}

	public LlaCoordinate getDroneHomeLocation() {
		return homeLocation;
	}

	public List<IFlightRoute> getDroneRouteAssignment() {
		return routeAssignment;
	}

	public void setDroneStartPoint(LlaCoordinate start) {
		startPoint = start;
	}

	public void setDroneHomeLocation(LlaCoordinate home) {
		homeLocation = home;
	}

	// public void setDroneRouteAssignment(ExportDroneRouteAssignment route) {
	// routeAssignment = route;
	// }

	public String getUAVId() {
		return UAVId;
	}
	//
	// public void setUAVId(String UAVId) {
	// this.UAVId = UAVId;
	// }

	public void addRoute(IFlightRoute route) {
		routeAssignment.add(route);

	}
}
