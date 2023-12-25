package edu.nd.dronology.monitoring.simplechecker.checker;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public class GeofenceRectangle {

	double lonStart;
	double lonEnd;
	double latStart;
	double latEnd;

	public GeofenceRectangle(double latStart, double latEnd, double lonStart, double lonEnd) {
		this.latStart = latStart;
		this.latEnd = latEnd;
		this.lonStart = lonStart;
		this.lonEnd = lonEnd;
	}

	public GeofenceRectangle(LlaCoordinate topLeft, LlaCoordinate bottomRight) {
		this(topLeft.getLatitude(), bottomRight.getLatitude(), topLeft.getLongitude(), bottomRight.getLongitude());
	}

	public boolean isInside(LlaCoordinate toCheck) {
		double lat = toCheck.getLatitude();
		double lon = toCheck.getLongitude();
		return lat < latStart && lat > latEnd && lon > lonStart && lon < lonEnd;

	}
}
