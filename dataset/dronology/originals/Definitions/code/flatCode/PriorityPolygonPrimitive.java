package edu.nd.dronology.services.extensions.areamapping.internal;


import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;

import javax.xml.transform.Source;

import edu.nd.dronology.services.extensions.areamapping.model.RiverSubsegment;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive.RouteType;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class PriorityPolygonPrimitive implements SearchPatternStrategy {
	private List<SourcePoints> sourcePoints;
	private List<Point2D.Double> priorityPolygonPoints;
	private RoutePrimitive.RouteType routeType;
	
	@Override
	public void setSourcePoints(List<SourcePoints> points) {
		sourcePoints = points;
	}
	
	@Override
	public void setRouteType(RouteType routeType) {
		this.routeType = routeType;
	}
	
	private List<Line2D.Double> findBoundingRectangleCrossingLines(Point2D.Double minPoint, Point2D.Double maxPoint, double APERATURE_HEIGHT, double OVERLAP_FACTOR){
		List<Line2D.Double> crossingLines = new ArrayList<>();
		Point2D.Double pointA = new Point2D.Double(minPoint.getX(), maxPoint.getY());
		Point2D.Double pointB = minPoint;
		Point2D.Double newPoint  = new Point2D.Double();
		double totalDistance = Geometry.findCartesianDistance(pointA, pointB);
		double traverseDistance = 10 * OVERLAP_FACTOR;
		crossingLines.add(new Line2D.Double(pointA, maxPoint));
		while(traverseDistance < totalDistance) {
			newPoint = Geometry.findOffsetPoint(pointA, pointB, traverseDistance / totalDistance);
			crossingLines.add(new Line2D.Double(newPoint, new Point2D.Double(maxPoint.getX(), newPoint.getY())));
			pointA = newPoint;
			totalDistance = totalDistance - traverseDistance;
		}
		crossingLines.add(new Line2D.Double(minPoint, new Point2D.Double(maxPoint.getX(),minPoint.getY())));
		return crossingLines;
	}
	
	private RoutePrimitive generateRouteWaypoints(List<Line2D.Double> crossingLines, SourcePoints sourcePoints) {
		RoutePrimitive newRoutePrimitive = new RoutePrimitive(routeType, sourcePoints.getWeight());
		Line2D.Double polygonSegment = new Line2D.Double();
		for(int i = 0; i < crossingLines.size(); i++) {
			List<Point2D.Double> intersectionPoints = new ArrayList<>();
			for(int j = 0; j < priorityPolygonPoints.size()-1; j++) {
				polygonSegment.setLine(priorityPolygonPoints.get(j), priorityPolygonPoints.get(j+1));
				if(crossingLines.get(i).intersectsLine(polygonSegment)) {
					intersectionPoints.add(Geometry.findLineIntersection(crossingLines.get(i), polygonSegment));
				}
			}
			//does this comparison thing work? 
			Collections.sort(intersectionPoints, Comparator.comparingDouble(Point2D.Double::getX));
			for(Point2D.Double entry : intersectionPoints) {
				//there is probably a better way to do this
				if(!newRoutePrimitive.getRoute().contains(entry)) {
					newRoutePrimitive.addRouteWaypoint(entry);
				}
			}
		}
		return newRoutePrimitive; 
	}

	@Override
	public List<RoutePrimitive> generateRoutePrimitive(double APERATURE_HEIGHT, double OVERLAP_FACTOR) {
		List<RoutePrimitive> priorityPolygonRoute = new ArrayList<>();
		List<SourcePoints> priorityAreas = new ArrayList<>();
		for(SourcePoints source : sourcePoints) {
			priorityPolygonPoints = new ArrayList<>(source.getSourcePoints());
			priorityPolygonPoints.add(priorityPolygonPoints.get(0));
			Vector<Point2D.Double> boundPoints = Geometry.simplePriorityPolygonBoundingRectangle(priorityPolygonPoints);
			List<Line2D.Double> crossingLines = findBoundingRectangleCrossingLines(boundPoints.get(0), boundPoints.get(1), APERATURE_HEIGHT, OVERLAP_FACTOR);
			RoutePrimitive newRoute = generateRouteWaypoints(crossingLines, source);
			priorityPolygonRoute.add(newRoute);
			Utilities.generateImageWaypoints(newRoute, APERATURE_HEIGHT, OVERLAP_FACTOR);
			
			//simply for printout visualization purposes
			SourcePoints area = new SourcePoints();
			area.setSourcePoints(priorityPolygonPoints);
			priorityAreas.add(area);
		}
		System.out.println("Priority area boundaries: ");
		Utilities.printSourcePoints(priorityAreas);
		Utilities.printRoutePrimitives(priorityPolygonRoute);
		Utilities.printImageWaypoints(priorityPolygonRoute);
		return priorityPolygonRoute;
	}
}
