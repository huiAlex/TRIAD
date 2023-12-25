package edu.nd.dronology.services.core.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public abstract class MappedItem implements IMappedItem, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2076504393371550022L;

	public MappedItem(String id) {
		this.id = id;
	}

	private String type;
	private String id;
	private String description;
	private Integer importance;
	private List<LlaCoordinate> coordinates = new ArrayList<>();

	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return type;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void setImportance(Integer importance) {
		this.importance = importance;
	}
	@Override
	public Integer getImportance() {
		return importance;
	} 
	@Override
	public void addCoordinate(LlaCoordinate coord) {
		coordinates.add(coord);
	}
	@Override
	public void setCoordinates(List<LlaCoordinate> coords) {
		coordinates = coords;
	}
	@Override
	public void removeCoodinate(LlaCoordinate coord) {
		coordinates.remove(coord);
	}
	@Override
	public List<LlaCoordinate> getCoordinates() {
		return Collections.unmodifiableList(coordinates);
	}
}
