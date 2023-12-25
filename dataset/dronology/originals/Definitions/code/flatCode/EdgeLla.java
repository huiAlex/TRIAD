package edu.nd.dronology.services.core.areamapping;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

/**
 * Implements Coordinate, but also has an edge member, which denotes the side of the mapping that a coordinate belongs to.
 * 
 * @author Andrew Slavin
 *
 */

public class EdgeLla extends LlaCoordinate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int side;
	
	
	public EdgeLla(double latitude, double longitude, double side) {
		super(latitude, longitude, 0);
		this.side = (int)side;
	}
	
	public int getSide() {
		return side;
	}
	
	public void setSide(int side) {
		this.side = side;
	}
	

}
