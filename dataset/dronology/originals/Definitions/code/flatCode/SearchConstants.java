package edu.nd.dronology.services.extensions.areamapping.output;

public class SearchConstants {
	private double OVERLAP_FACTOR;
	private double APERATURE_WIDTH;
	private double MAX_RIVER_WIDTH;
	private int dronesNum;
	private static SearchConstants instance = null;
	
	public static SearchConstants getInstance() {
		if(instance == null) {
			instance = new SearchConstants();
		}
		return instance;
	}
	public void initialize(double overlapFactor, double aperatureWidth, double maxRiverWidth, int drones) {
		OVERLAP_FACTOR = overlapFactor;
		APERATURE_WIDTH = aperatureWidth;
		MAX_RIVER_WIDTH = maxRiverWidth;
		dronesNum = drones;
	}
	
	public double getOverlapFactor() {
		return OVERLAP_FACTOR;
	}
	
	public double getAperatureWidth() {
		return APERATURE_WIDTH;
	}
	
	public double getMaxRiverWidth() {
		return MAX_RIVER_WIDTH;
	}
	
	public int getDronesNum() {
		return dronesNum;
	}
}
