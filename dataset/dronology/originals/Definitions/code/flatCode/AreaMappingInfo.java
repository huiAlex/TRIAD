
package edu.nd.dronology.services.core.info;

import java.util.LinkedList;
import java.util.Vector;

import edu.nd.dronology.services.core.areamapping.EdgeLla;

public class AreaMappingInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8659534015844582331L;
	private Vector<LinkedList<EdgeLla>> coordinates = new Vector<>();
	private long dateCreated;
	private long dateModified;
	private double length;
	private String description;
	
	public AreaMappingInfo(String name, String id) {
		super(name, id);
		coordinates.add(0, new LinkedList<>());
		coordinates.add(1, new LinkedList<>());
	}
	
	public double getMappingLength() {
		return length;
	}
	
	public void addCoordinate(int part, EdgeLla coordinate) {
		coordinates.get(part).add(coordinate);
	}
	
	public void removeCoordinate(int part, EdgeLla coordinate) {
		coordinates.get(part).remove(coordinate);
	}
	
	public LinkedList<EdgeLla> getCoordinates(int part) {
		return coordinates.get(part);
	}

	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public long getDateModified() {
		return dateModified;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
