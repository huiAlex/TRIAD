package edu.nd.dronology.services.core.items;

import java.util.List;
import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IAreaMapping extends IPersistableItem {

	String getDescription();

	void setDescription(String description);

	List<EdgeLla> getMappedPoints(Integer part);

	void addMappedArea(Integer part, List<EdgeLla> coordinates);

	void addMappedArea(Integer part, EdgeLla coordinate);

	void removeCoordinate(int part, EdgeLla coordinate);

	void addMappedLocation(IMappedItem item);
	
	List<IMappedItem> getLocationMappings();

	void removeMappedLocation(String id) throws DronologyServiceException;
	
	void setUpstream(Boolean upstream);
	
	Boolean getUpstream();
}
