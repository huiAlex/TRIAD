package edu.nd.dronology.services.extensions.areamapping.internal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.neuralnet.twod.util.TopographicErrorHistogram;
import org.apache.logging.log4j.core.appender.routing.Route;

import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive.RouteType;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class AdaptedCreepingLinePrimitive implements SearchPatternStrategy{
	
	private List<SourcePoints> sourcePoints;
	private RoutePrimitive.RouteType routeType;
	
	public AdaptedCreepingLinePrimitive() {
		sourcePoints = new ArrayList<>();
	}
	
	@Override
	public void setSourcePoints(List<SourcePoints> points) {
		sourcePoints = points;
	}
	
	public void setRouteType(RoutePrimitive.RouteType routeType) {
		this.routeType = routeType;
	}
	
	
	/**
	 * This method generates the creeping line route between the source points.
	 * @param sideA
	 * @param sideB
	 * @return RoutePrimitive
	 */
	private RoutePrimitive generateCreepingLineRoute(SourcePoints sideA, SourcePoints sideB) {
		int opposingRouteIndex = 0;
		int firstOpposingRouteIndex = 0;
		int lastOpposingRouteIndex = 0;
		boolean missedFirstPoints = false;
		boolean missedLastPoints = false;
		RoutePrimitive newRoute = new RoutePrimitive(routeType, sideA.getWeight());
		Point2D.Double startPoint = new Point2D.Double();
		Point2D.Double opposingPoint = new Point2D.Double();
		Point2D.Double previousOpposingPoint = new Point2D.Double();
		//main for loop for adding waypoints to the route primitive
		for(int i = 0; i < sideA.size(); i++) {
			startPoint = sideA.getSourcePoint(i);
			newRoute.addRouteWaypoint(startPoint);
			opposingPoint = Geometry.findClosestOpposingPoint(startPoint, sideB.getSourcePoints());
			if(i > 0) {
				if(sideB.getSourcePoints().indexOf(opposingPoint) - opposingRouteIndex > 1) {
					generateCreepingLineMiddle(sideA, sideB, newRoute, opposingRouteIndex, sideB.getSourcePoints().indexOf(opposingPoint));
				}
			}
			opposingRouteIndex = sideB.getSourcePoints().indexOf(opposingPoint);
			if(i == 0) {
				if(opposingRouteIndex > 0) {
					missedFirstPoints = true;
					firstOpposingRouteIndex = opposingRouteIndex;
				}
			}
			//System.out.println("index of opposing point: " + opposingRouteIndex);
			//do I want this portion? --- attempts to minimize doubling of routes
			if(opposingPoint == previousOpposingPoint && i < sideA.size()-1) {
				previousOpposingPoint = new Point2D.Double();
			} else {
				newRoute.addRouteWaypoint(opposingPoint);
				previousOpposingPoint = opposingPoint;
			}
		}
		//checking for any missed waypoint nodes
		if(opposingRouteIndex != sideB.size()) {
			missedLastPoints = true;
			lastOpposingRouteIndex = opposingRouteIndex;
		}
		if(missedFirstPoints) {
			generateCreepingLineBeginning(sideA, sideB, newRoute, firstOpposingRouteIndex);
		}
		if(missedLastPoints) {
			generateCreepinLineEnd(sideA, sideB, newRoute, lastOpposingRouteIndex);
		} 
		return newRoute;
	}
	
	private void generateCreepingLineMiddle(SourcePoints sideA, SourcePoints sideB, RoutePrimitive newRoute, int previousOpposingRouteIndex, int newOpposingRouteIndex) {
		Point2D.Double pointA = newRoute.getRouteEndPoint();
		if((newOpposingRouteIndex - previousOpposingRouteIndex) % 2 == 0) {
			newRoute.addRouteWaypoint(sideB.getSourcePoint(previousOpposingRouteIndex + 1));
			if(newOpposingRouteIndex - previousOpposingRouteIndex > 1) {
				for(int i = previousOpposingRouteIndex + 2; i < newOpposingRouteIndex; i++) {
					newRoute.addRouteWaypoint(sideB.getSourcePoint(i));
					newRoute.addRouteWaypoint(pointA);
					newRoute.addRouteWaypoint(sideB.getSourcePoint(i+1));
					i++;
				}
			}
		} else {
			for(int i = previousOpposingRouteIndex + 1; i < newOpposingRouteIndex; i++) {
				newRoute.addRouteWaypoint(sideB.getSourcePoint(i));
				newRoute.addRouteWaypoint(sideB.getSourcePoint(i+1));
				newRoute.addRouteWaypoint(pointA);
				i++;
			}
		}
	}
	
	
	/**
	 * This is a helper method for generateCreepingLineRoute() to handle any source points 
	 * missed at the beginning of the route.
	 * @param sideA
	 * @param sideB
	 * @param newRoute
	 * @param firstOpposingRouteIndex
	 */
	private void generateCreepingLineBeginning(SourcePoints sideA, SourcePoints sideB, RoutePrimitive newRoute, int firstOpposingRouteIndex) {
		if(firstOpposingRouteIndex % 2 == 0) {
			int counter = 0;
			for(int i = 0; i < firstOpposingRouteIndex; i++) {
				newRoute.insertRouteWaypoint(counter, sideA.getSourcePoint(0));
				newRoute.insertRouteWaypoint(counter + 1, sideB.getSourcePoint(i));
				newRoute.insertRouteWaypoint(counter + 2, sideB.getSourcePoint(i+1));
				counter += 3;
				i++;
			}
		} else {
			int counter = 1;
			newRoute.insertRouteWaypoint(0, sideB.getSourcePoint(0));
			if(firstOpposingRouteIndex > 1) {
				for(int i = 1; i < firstOpposingRouteIndex; i++) {
					newRoute.insertRouteWaypoint(counter, sideA.getSourcePoint(0));
					newRoute.insertRouteWaypoint(counter + 1, sideB.getSourcePoint(i));
					newRoute.insertRouteWaypoint(counter + 1, sideB.getSourcePoint(i+1));
					counter += 2;
					i++;
				}
			}
		}
	}
	
	
	/**
	 * This is a helper method for generateCreepingLineRoute() to handle and source point 
	 * missed at the end of the route.
	 * @param sideA
	 * @param sideB
	 * @param newRoute
	 * @param lastOpposingRouteIndex
	 */
	private void generateCreepinLineEnd(SourcePoints sideA, SourcePoints sideB, RoutePrimitive newRoute, int lastOpposingRouteIndex) {
		for(int i = lastOpposingRouteIndex + 1; i < sideB.size(); i++) {
			newRoute.addRouteWaypoint(sideB.getSourcePoint(i));
			newRoute.addRouteWaypoint(sideA.getSourcePoint(sideA.size()-1));
			if(i < sideB.size()-1) {
				newRoute.addRouteWaypoint(sideB.getSourcePoint(i+1));
			}
			i++;
		}
	}
	
	@Override
	public List<RoutePrimitive> generateRoutePrimitive(double APERATURE_HEIGHT, double OVERLAP_FACTOR){
		List<RoutePrimitive> routes = new ArrayList<>();
		SourcePoints source1 = sourcePoints.get(0);
		SourcePoints source2 = sourcePoints.get(1);
		RoutePrimitive creepingLineRoute;
		if(source1.size() < source2.size()) {
			creepingLineRoute = generateCreepingLineRoute(source2, source1);
		} else {
			creepingLineRoute = generateCreepingLineRoute(source1, source2);
		}
		//NOTE: should actually be APERATURE_HEIGHT
		Utilities.generateImageWaypoints(creepingLineRoute, APERATURE_HEIGHT, OVERLAP_FACTOR);
		routes.add(creepingLineRoute);
		return routes;
	}
}
