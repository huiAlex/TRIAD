package edu.nd.dronology.services.core.areamapping;

import java.util.List;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

public interface IRegionOfInterest {

	String getName();

	String getDescription();

	double getWeight();

	List<LlaCoordinate> getArea();
	
	String getAreaAttribute(String attributeName);

}