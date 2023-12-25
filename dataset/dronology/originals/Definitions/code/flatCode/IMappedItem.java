package edu.nd.dronology.services.core.items;

import java.util.List;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public interface IMappedItem {

	String getId();

	String getDescription();

	void setDescription(String description);

	void setId(String id);

	String getType();

	void setType(String type);
	
	void setImportance(Integer importance);
	
	Integer getImportance();

	void addCoordinate(LlaCoordinate coord);
	
	void setCoordinates(List<LlaCoordinate> coords);

	void removeCoodinate(LlaCoordinate coord);

	List<LlaCoordinate> getCoordinates();

}
