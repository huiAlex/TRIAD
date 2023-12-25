package edu.nd.dronology.services.extensions.areamapping.selection;

import java.util.Collection;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IRouteSelectionStrategy {

	void initialize(GeneratedRoutesInfo info, Collection<IUAVProxy> uavs, IAreaMapping mapping)
			throws DronologyServiceException;

	RouteSelectionResult generateAssignments() throws DronologyServiceException;

	RouteSelectionResult generateAssignments(int numAssignments) throws DronologyServiceException;

}