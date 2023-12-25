package edu.nd.dronology.services.extensions.areamapping.internal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive.RouteType;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class RiverBankPrimitive implements SearchPatternStrategy{
	private List<SourcePoints> sourcePointsList;
	private RoutePrimitive.RouteType routeType;
	
	public RiverBankPrimitive() {
		sourcePointsList = new ArrayList<>();
	}
	
	@Override
	public void setSourcePoints(List<SourcePoints> points) {
		sourcePointsList = points;
	}
	
	@Override
	public void setRouteType(RoutePrimitive.RouteType routeType) {
		this.routeType = routeType;
	}
	
	private RoutePrimitive transformSourcePoints(SourcePoints sourcePoints, double APERATURE_HEIGHT, double OVERLAP_FACTOR) {
		RoutePrimitive newRoute = new RoutePrimitive(routeType, sourcePoints.getWeight());
		for(Point2D.Double entry : sourcePoints.getSourcePoints()) {
			newRoute.addRouteWaypoint(entry);
		}
		Utilities.generateImageWaypoints(newRoute, APERATURE_HEIGHT, OVERLAP_FACTOR);
		return newRoute;
	}
	
	@Override
	public List<RoutePrimitive> generateRoutePrimitive(double APERATURE_HEIGHT, double OVERLAP_FACTOR){

		List<RoutePrimitive> routes = new ArrayList<>();
		for(SourcePoints source : sourcePointsList) {
			routes.add(transformSourcePoints(source, APERATURE_HEIGHT, OVERLAP_FACTOR));
		}
		return routes;
	}
}
