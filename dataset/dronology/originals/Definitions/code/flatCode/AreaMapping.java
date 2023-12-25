package edu.nd.dronology.services.core.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.nd.dronology.core.util.ManagedHashTableList;
import edu.nd.dronology.services.core.areamapping.EdgeLla; 
import edu.nd.dronology.services.core.areamapping.IRegionOfInterest;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * 
 * @author Michael Vierhauser
 *
 */ 
public class AreaMapping implements IAreaMapping {

	private String name; 
	private String id;
	private String description;
	private ManagedHashTableList<Integer, EdgeLla> areaMappings = new ManagedHashTableList<>();
	private List<IMappedItem> locationMappings = new ArrayList<>();
	private Boolean upstream;

	// MappedArea area;
	List<IRegionOfInterest> regions = new ArrayList<>();

	public AreaMapping() {
		id = UUID.randomUUID().toString();
		name = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public List<EdgeLla> getMappedPoints(Integer part) {
		return Collections.unmodifiableList(areaMappings.get(part));
	}

	@Override
	public void addMappedArea(Integer part, List<EdgeLla> coordinates) {
		areaMappings.put(part, coordinates);
	}

	@Override
	public void addMappedArea(Integer part, EdgeLla coordinate) {
		areaMappings.add(part, coordinate);
	}

	@Override
	public void removeCoordinate(int part, EdgeLla coordinate) {
		int index = areaMappings.get(part).indexOf(coordinate);
		if (index != -1) {
			areaMappings.get(part).remove(coordinate);
		}
	}

	@Override
	public void addMappedLocation(IMappedItem item) {
		locationMappings.add(item);
	}
	
	@Override
	public List<IMappedItem> getLocationMappings() {
		return locationMappings;
	}


	@Override
	public void removeMappedLocation(String id) throws DronologyServiceException {
		List<IMappedItem> toCheck = new ArrayList<>(locationMappings);
		for (IMappedItem item : toCheck) {
			if (item.getId().equals(id)) {
				locationMappings.remove(item);
				return;
			}
		}
		throw new DronologyServiceException("Location with id '" + id + "' not found!");
	}

	@Override
	public void setUpstream(Boolean upstream) {
		this.upstream = upstream;
	}

	@Override
	public Boolean getUpstream() {
		return upstream;
	}

}
