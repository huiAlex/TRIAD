package edu.nd.dronology.services.extensions.areamapping.creation;

import java.awt.geom.Path2D.Double;
import java.util.List;

import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;

public interface IRouteCreator {

	List<RoutePrimitive> generateRoutePrimitives();

	List<RiverBank> getBankList();

	Double getTotalRiverSegment();

	double getAverageLatitude();

}
