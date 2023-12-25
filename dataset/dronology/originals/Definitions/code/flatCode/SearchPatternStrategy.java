package edu.nd.dronology.services.extensions.areamapping.internal;

import java.util.List;

import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;

public interface SearchPatternStrategy {
	public List<RoutePrimitive> generateRoutePrimitive(double APERATURE_HEIGHT, double OVERLAP_FACTOR);

	public void setSourcePoints(List<SourcePoints> points);
	
	public void setRouteType(RoutePrimitive.RouteType routeType);
}
