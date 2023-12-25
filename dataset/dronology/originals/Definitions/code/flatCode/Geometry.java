package edu.nd.dronology.services.extensions.areamapping.internal;




import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.nd.dronology.services.extensions.areamapping.model.RiverSubsegment;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;
public class Geometry {

    static final double RADIUS = 6373000.0;
    public static final double EPSILON = 0.000001;

    /**
     * This function calculates the angle between two points.
     * @param a
     * @param b
     * @return angle between two points
     */
    public static double getAngle(Point2D.Double a, Point2D.Double b) {
        double angle = 0;
        if(!(a.getX() == b.getX() || a.getY() == b.getY()))
        {
            angle = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()) ;
        }
        return angle;
    }

    /**
     * This function finds the midpoint between two points.
     * @param a
     * @param b
     * @return midpoint between two points.
     */
    public static Point2D.Double findMidpoint(Point2D.Double a, Point2D.Double b) {
        Point2D.Double mid = new Point2D.Double((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);
        return mid;
    }


    /**
     * This function takes a point of gps coordinates and translates into cartesian coordinates.
     * X is latitude, Y is longitude, and centralLatitude is the latitude of a point in the river.
     * @param gpsPoint
     * @param centralLatitude
     * @return Point2D.Double of cartesian coordinates
     */
    public static Point2D.Double gpsToCartesian (Point2D.Double gpsPoint, double centralLatitude){
        Point2D.Double cartesianPoint = new Point2D.Double();

        double lat = Math.toRadians(gpsPoint.getX());
        double lon = Math.toRadians(gpsPoint.getY());
        double aspectRatio = Math.cos(Math.toRadians(centralLatitude));

        double xCartesian = RADIUS * lat;
        double yCartesian = RADIUS * lon * aspectRatio;

        cartesianPoint = new Point2D.Double(xCartesian,yCartesian);
        return cartesianPoint;
    }

    /*This function is the inverse of gpsToCartesian. */

    /**
     * This function performs the reverse operation of gpsToCartesian. It takes a point of cartesian coordinates
     * and translates them into gps coordinates.
     * @param cartesianPoint
     * @param centralLatitude
     * @return Point2D.Double of gps coordinates
     */
    public static Point2D.Double cartesianToGPS (Point2D.Double cartesianPoint, double centralLatitude) {
        Point2D.Double gpsPoint = new Point2D.Double();

        double xCartesian = cartesianPoint.getX();
        double yCartesian = cartesianPoint.getY();
        double aspectRatio = Math.cos(Math.toRadians(centralLatitude));

        double lat = Math.toDegrees(xCartesian / RADIUS);
        double lon = Math.toDegrees(yCartesian / (RADIUS * aspectRatio));

        gpsPoint = new Point2D.Double(lat, lon);
        return gpsPoint;
    }

    /**
     * This function finds the intersection point of two line segments
     * @param line1
     * @param line2
     * @return intersection point of the two line segments
     */
    public static Point2D.Double findLineIntersection(Line2D.Double line1, Line2D.Double line2){
        double m1, m2, b1, b2, xIntersection, yIntersection;
        
        m1 = (line1.getP2().getY() - line1.getP1().getY()) / (line1.getP2().getX() - line1.getP1().getX());
        m2 = (line2.getP2().getY() - line2.getP1().getY()) / (line2.getP2().getX() - line2.getP1().getX());
        
        if (m1 > 1000 || m1 < -1000){
            xIntersection = line1.getP1().getX();
            b2 = line2.getP1().getY() - (m2 * line2.getP1().getX());
            yIntersection = m2 * xIntersection + b2;
        }
        else if (m2 > 1000 || m2 < -1000){
            xIntersection = line2.getP1().getX();
            b1 = line1.getP1().getY() - (m1 * line1.getP1().getX());
            yIntersection = m1 * xIntersection + b1;
        }

        else if (m1 < EPSILON && m1 > -EPSILON) { //horizontal line
            yIntersection = line1.getP2().getY();
            b2 = line2.getP1().getY() - (m2 * line2.getP1().getX());
            xIntersection = (yIntersection - b2) / m2;
        }
        
        else if (m2 < EPSILON && m2 > -EPSILON) { //horizontal line
            yIntersection = line2.getP2().getY();
            b1 = line1.getP1().getY() - (m1 * line1.getP1().getX());
            xIntersection = (yIntersection - b1) / m1;
        }

        else {
            b1 = line1.getP1().getY() - (m1 * line1.getP1().getX());
            b2 = line2.getP1().getY() - (m2 * line2.getP1().getX());
            xIntersection = (b2-b1) / (m1-m2);
            yIntersection = m1 * ( (b2 - b1) / (m1 - m2) ) + b1;
        }

        return new Point2D.Double(xIntersection,yIntersection);
    }
    
    
    /**
     * This function finds the cartesian distance between two points
     * @param pointA
     * @param pointB
     * @return cartesian distance
     */
    public static double findCartesianDistance(Point2D.Double pointA, Point2D.Double pointB) {
    	return Math.sqrt(Math.pow((pointA.getX() - pointB.getX()), 2) + Math.pow((pointA.getY() - pointB.getY()), 2));
    }
    
    
    /**
     * This function uses a distance ratio to calculate a point a given distance along the path from one point to another
     * @param point1
     * @param point2
     * @param distanceRatio - desired distance/distance between the two points
     * @return
     */
    public static Point2D.Double findOffsetPoint(Point2D.Double point1, Point2D.Double point2, double distanceRatio){
    	Point2D.Double newPoint = new Point2D.Double();
    	newPoint.setLocation(((1-distanceRatio)*point1.getX() + distanceRatio*point2.getX()), ((1-distanceRatio)*point1.getY() + distanceRatio*point2.getY()));
    	return newPoint;
    }
    
    
    /**
     * This function calculates the total cartesian distance of a RoutePrimitive
     * @param route
     * @return cartesian distance
     */
    public static double routePrimitiveDistance(List<Point2D.Double> route) {
    	double totalDistance = 0;
    	for(int i = 0; i < route.size()-1; i++) {
    		totalDistance += findCartesianDistance(route.get(i), route.get(i+1));
    	}
    	return totalDistance;
    }
    
    
    /**
     * This function calculates the total cartesian distance of a RiverBank
     * @param bank
     * @return cartesian distance
     */
    public static double riverBankDistance(RiverBank bank) {
    	double totalDistance = 0;
    	for(int i = 0; i < bank.size()-1; i++) {
    		totalDistance += findCartesianDistance(bank.get(i), bank.get(i+1));
    	}
    	return totalDistance;
    }
    
    
    /**
     * This function calculates the area irregular polygon shape of the river segment
     * @param segment
     * @return area
     */
    public static double calculateRiverSegmentArea(RiverSubsegment segment) {
    	double area = 0;
    	List<Point2D.Double> bank1 = segment.get(0).get();
    	for(int i = 0; i < bank1.size()-1; i++) {
    		area += bank1.get(i).getY()*bank1.get(i+1).getX() - bank1.get(i).getX()*bank1.get(i+1).getY();
    	}
    	RiverBank bank2 = segment.get(1);
    	bank2.reverse();
    	area += bank1.get(bank1.size()-1).getY()*bank2.get(0).getX() - bank1.get(bank1.size()-1).getX()*bank2.get(0).getY();
    	for(int i = 0; i < bank2.size()-1; i++) {
    		area += bank2.get(i).getY()*bank2.get(i+1).getX() - bank2.get(i).getX()*bank2.get(i+1).getY();
    	}
    	area += bank2.get(bank2.size()-1).getY()*bank1.get(0).getX() - bank2.get(bank2.size()-1).getX()*bank1.get(0).getY();
    	area = area / 2;
    	bank2.reverse();    	
    	return Math.abs(area);
    }
    
    
    /**
     * This function calculates the maximum and minimum coordinate points of a simpleBoundingRectangle around the 
     * river segment
     * @param banks
     * @return vector containing maximum and minimum coordinate points
     */
    public static Vector<Point2D.Double> simpleRiverBoundingRectangle(List<RiverBank> banks) {
    	Vector<Point2D.Double> minMax = new Vector<>();
    	List<Point2D.Double> bank1 = banks.get(0).get();
    	List<Point2D.Double> bank2 = banks.get(1).get();
    	Point2D.Double minPoint = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
    	Point2D.Double maxPoint = new Point2D.Double(-Double.MAX_VALUE, -Double.MAX_VALUE);
    	minAndMaxPoints(minPoint, maxPoint, bank1);
    	minAndMaxPoints(minPoint, maxPoint, bank2);
    	minMax.add(minPoint);
    	minMax.add(maxPoint);
    	/*System.out.println("[" + minPoint.getX() + ", " + minPoint.getY() + "]");
    	System.out.println("[" + minPoint.getX() + ", " + maxPoint.getY() + "]");
    	System.out.println("[" + maxPoint.getX() + ", " + maxPoint.getY() + "]");
    	System.out.println("[" + maxPoint.getX() + ", " + minPoint.getY() + "]");*/
    	return minMax;
    }
    
    public static Vector<Point2D.Double> simplePriorityPolygonBoundingRectangle(List<Point2D.Double> polygonPoints) {
    	Vector<Point2D.Double> minMax = new Vector<>();
    	Point2D.Double minPoint = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
    	Point2D.Double maxPoint = new Point2D.Double(-Double.MAX_VALUE, -Double.MAX_VALUE);
    	minAndMaxPoints(minPoint, maxPoint, polygonPoints);
    	minMax.add(minPoint);
    	minMax.add(maxPoint);
    	/*System.out.println("[" + minPoint.getX() + ", " + minPoint.getY() + "]");
    	System.out.println("[" + minPoint.getX() + ", " + maxPoint.getY() + "]");
    	System.out.println("[" + maxPoint.getX() + ", " + maxPoint.getY() + "]");
    	System.out.println("[" + maxPoint.getX() + ", " + minPoint.getY() + "]");*/
    	return minMax;
    }
    
   
    /**
     * This function finds the minimum and maximum points in a RiverBank
     * @param minPoint
     * @param maxPoint
     * @param points
     */
    private static void minAndMaxPoints(Point2D.Double minPoint, Point2D.Double maxPoint, List<Point2D.Double> points) {
    	for(int i = 0; i < points.size(); i++) {
    		if(points.get(i).getX() < minPoint.getX()) {
    			minPoint.setLocation(points.get(i).getX(), minPoint.getY());
    		}
    		if(points.get(i).getX() > maxPoint.getX()) {
    			maxPoint.setLocation(points.get(i).getX(),maxPoint.getY());
    		}
    		if(points.get(i).getY() < minPoint.getY()) {
    			minPoint.setLocation(minPoint.getX(), points.get(i).getY());
    		}
    		if(points.get(i).getY() > maxPoint.getY()) {
    			maxPoint.setLocation(maxPoint.getX(), points.get(i).getY());
    		}
    	}
    }
    
    
    /**
     * This function finds the closest point in the opposite RoutePrimitive
     * @param point
     * @param opposingSide
     * @return closest opposing point
     */
    public static Point2D.Double findClosestOpposingPoint(Point2D.Double point, List<Point2D.Double> opposingSide) {
    	Point2D.Double destination = new Point2D.Double();
    	double distance = Double.MAX_VALUE;
    	double newDistance;
    	for(Point2D.Double node : opposingSide) {
    		newDistance = findCartesianDistance(point, node);
    		if(newDistance < distance){
    			distance = newDistance;
    			destination = node;
    		}
    	}
    	return destination;
    } 
    
    
    /**
     * This function inserts additional nodes into each RiverBank so that there is a node every 
     * APERATURE_WIDTH*OVERLAP_FACTOR.
     * @param bank
     * @param newPoints
     * @param APERATURE_WIDTH
     * @param OVERLAP_FACTOR
     * @param riverSegment
     * @return adjusted RiverBank
     */
    private static RiverBank findRiverBankOffset(RiverBank bank, List<Point2D.Double> newPoints, double APERATURE_WIDTH, double OVERLAP_FACTOR, Path2D.Double riverSegment){
		Point2D.Double newPoint = new Point2D.Double();
		Point2D.Double point1;
		Point2D.Double point2;
		RiverBank newBank = new RiverBank();
		double riverBankDistance;
		double traverseDistance = APERATURE_WIDTH*OVERLAP_FACTOR;
		double distanceRatio;
		double newTraverseDistance = 0;
		for(int i = 0; i < bank.size()-1; i++) {
			point1 = bank.get(i);
			point2 = bank.get(i+1);
			newBank.add(point1);
			riverBankDistance = Geometry.findCartesianDistance(point1, point2);
			if(newTraverseDistance != 0 && newTraverseDistance <= riverBankDistance) {
				distanceRatio = newTraverseDistance / riverBankDistance;
				newPoint = Geometry.findOffsetPoint(point1, point2, distanceRatio);
				newBank.add(newPoint);
				newPoints.add(newPoint);
				point1 = newPoint;
				riverBankDistance = riverBankDistance - newTraverseDistance;
				newTraverseDistance = 0;
			}
			while(traverseDistance <= riverBankDistance) {
					distanceRatio = traverseDistance / riverBankDistance;
					newPoint = Geometry.findOffsetPoint(point1, point2, distanceRatio);
					newBank.add(newPoint);
					newPoints.add(newPoint);
					riverBankDistance = riverBankDistance - traverseDistance;
					point1 = newPoint;
			} 
				newTraverseDistance = traverseDistance - riverBankDistance;
		}
		return newBank;
	}
    
    private static void sourcePointsStartAndEnd(Point2D.Double bank1Point, Point2D.Double bank2Point, SourcePoints newPoints, double APERATURE_HEIGHT, double OVERLAP_FACTOR) {
    	double traverseDistance = APERATURE_HEIGHT / 2;
    	double totalDistance = findCartesianDistance(bank1Point, bank2Point);
    	newPoints.addSourcePoint(findOffsetPoint(bank1Point, bank2Point, traverseDistance / totalDistance));
    }
    
    //trying to fix issue with whiteField.json
    /*private static Point2D.Double getProjectedPoint(double APERATURE_HEIGHT, double thetaO, Path2D.Double riverSegment, Point2D.Double bankNode){
    	double dx = (APERATURE_HEIGHT / 2) * Math.cos(thetaO);
		double dy = (APERATURE_HEIGHT / 2) * Math.sin(thetaO);
		double newX = bankNode.getX()+dx;
		double newY = bankNode.getY()+dy;
		Point2D.Double newPoint  = new Point2D.Double(newX, newY);
		if(!riverSegment.contains(newPoint)) {
			newPoint.setLocation(bankNode.getX()-dx, bankNode.getY()-dy);
		}
		if(!riverSegment.contains(newPoint)) {
			thetaO = thetaO - Math.PI;
			dx = (APERATURE_HEIGHT / 2) * Math.cos(thetaO);
			dy = (APERATURE_HEIGHT / 2) * Math.sin(thetaO);
			newX = bankNode.getX()+dx;
			newY = bankNode.getY()+dy;
			newPoint.setLocation(newX, newY);
		}
		if(!riverSegment.contains(newPoint)) {
			newPoint.setLocation(bankNode.getX()-dx, bankNode.getY()-dy);
		}
		if(!riverSegment.contains(newPoint)){
			System.out.println("couldn't find a projected point");
			return null;
		} else {
			return newPoint;
		}
    } */
    
    
    /**
     * This function projects the points in RiverBank out into the river segment and makes these points 
     * into a RoutePrimitive.
     * @param bank1
     * @param newPoints
     * @param APERATURE_WIDTH
     * @param OVERLAP_FACTOR
     * @param riverSegment
     * @return RoutePrimitive
     */
    private static SourcePoints projectPointsInwards(RiverBank bank1, RiverBank bank2, List<Point2D.Double> newPoints, double APERATURE_HEIGHT, double OVERLAP_FACTOR, Path2D.Double riverSegment) {
		SourcePoints newSourcePoints = new SourcePoints();
		double dx;
		double dy;
		double newX;
		double newY;
		HashSet<Point2D.Double> nodes = new HashSet<>(newPoints);
		sourcePointsStartAndEnd(bank1.get(0), bank2.get(0), newSourcePoints, APERATURE_HEIGHT, OVERLAP_FACTOR);
		for(int i = 0; i < bank1.size()-2; i++) {
			if(!nodes.contains(bank1.get(i+1))) {
				continue;
			}
			double theta1 = Geometry.getAngle(bank1.get(i), bank1.get(i+1));
			double theta2 = Geometry.getAngle(bank1.get(i+1), bank1.get(i+2));
			double theta = (theta1 + theta2) / 2;
			double thetaO;
			//this was trying to fix issue with whiteField.json
			/*if(theta1 == 0.0 && theta2 == 0.0) {
				thetaO = theta;
			} else {
				thetaO = theta + Math.PI / 2;
			}*/
			thetaO = theta + Math.PI / 2;
			dx = (APERATURE_HEIGHT / 2) * Math.cos(thetaO);
			dy = (APERATURE_HEIGHT / 2) * Math.sin(thetaO);
			newX = bank1.get(i+1).getX()+dx;
			newY = bank1.get(i+1).getY()+dy;
			Point2D.Double newPoint  = new Point2D.Double(newX, newY);
			if(!riverSegment.contains(newPoint)) {
				newPoint.setLocation(bank1.get(i+1).getX()-dx, bank1.get(i+1).getY()-dy);
			}
			if(!riverSegment.contains(newPoint)) {
				thetaO = theta - Math.PI / 2;
				dx = (APERATURE_HEIGHT / 2) * Math.cos(thetaO);
				dy = (APERATURE_HEIGHT / 2) * Math.sin(thetaO);
				newX = bank1.get(i+1).getX()+dx;
				newY = bank1.get(i+1).getY()+dy;
				newPoint.setLocation(newX, newY);
			}
			if(!riverSegment.contains(newPoint)) {
				newPoint.setLocation(bank1.get(i+1).getX()-dx, bank1.get(i+1).getY()-dy);
			}
			if(riverSegment.contains(newPoint)) {
				newSourcePoints.addSourcePoint(newPoint);
			} 
		}
		sourcePointsStartAndEnd(bank1.get(bank1.size()-1), bank2.get(bank2.size()-1), newSourcePoints, APERATURE_HEIGHT, OVERLAP_FACTOR);
		return newSourcePoints;
	}
    
    
    /**
     * This function creates route primitives parallel to the riverbanks inside the river segment.
     * @param APERATURE_WIDTH
     * @param OVERLAP_FACTOR
     * @param bankList
     * @return RoutePrimitive objects for both riverbanks
     */
    public static List<SourcePoints> generateSourcePoints(double APERATURE_WIDTH, double APERATURE_HEIGHT, double OVERLAP_FACTOR, List<RiverBank> bankList, boolean innerCrissCross){
    	List<SourcePoints> sourcePointsList = new ArrayList<>();
    	sourcePointsList.add(new SourcePoints());
    	sourcePointsList.add(new SourcePoints());
    	List<Point2D.Double> newPoints = new ArrayList<>();
		List<Point2D.Double> newPoints1 = new ArrayList<>();
		RiverBank bank1;
		RiverBank bank2;
		Path2D.Double riverSegment = Utilities.makeTotalRiverSegment(bankList);
		RiverSubsegment newRiver = new RiverSubsegment();

		bank1 = bankList.get(0);
		bank2 = bankList.get(1);
		bank1 = findRiverBankOffset(bank1, newPoints, APERATURE_WIDTH, OVERLAP_FACTOR, riverSegment);
		bank2 = findRiverBankOffset(bank2, newPoints1, APERATURE_WIDTH, OVERLAP_FACTOR, riverSegment);
		newRiver.add(bank1);  //what does this line actually do?
		newRiver.add(bank2);

		if(innerCrissCross) {
			sourcePointsList.set(0, projectPointsInwards(bank1, bank2, newPoints, APERATURE_HEIGHT * 2, OVERLAP_FACTOR, riverSegment));
			sourcePointsList.set(1, projectPointsInwards(bank2, bank1, newPoints1, APERATURE_HEIGHT * 2, OVERLAP_FACTOR, riverSegment));
		} else {
			sourcePointsList.set(0, projectPointsInwards(bank1, bank2, newPoints, APERATURE_HEIGHT, OVERLAP_FACTOR, riverSegment));
			sourcePointsList.set(1, projectPointsInwards(bank2, bank1, newPoints1, APERATURE_HEIGHT, OVERLAP_FACTOR, riverSegment));
		}
		return sourcePointsList;
    }
}