package edu.nd.dronology.services.extensions.areamapping.internal;

import java.awt.geom.Point2D;


public class ImageWaypoint {
	private Point2D.Double waypoint;
	private double orientationAngle;
	
	public ImageWaypoint() {
		waypoint = new Point2D.Double();
	}
	
	public ImageWaypoint(Point2D.Double point, double angle) {
		waypoint = new Point2D.Double();
		setWaypoint(point.getX(), point.getY());
		setOrientationAngle(angle);
	}
	
	public Point2D.Double getWaypoint() {
		return waypoint;
	}
	
	public double getOrientationAngle() {
		return orientationAngle;
	}
	
	public void setImageWaypoint(double latitude, double longitude, double angle) {
		setWaypoint(latitude, longitude);
		setOrientationAngle(angle);
	}
	
	public void setWaypoint(double latitude, double longitude) {
		waypoint.setLocation(latitude, longitude);
	}
	
	public void setOrientationAngle(double angle) {
		orientationAngle = angle;
	}
}
