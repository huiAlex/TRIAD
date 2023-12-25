package edu.nd.dronology.services.extensions.areamapping.instances;

import java.util.Collection;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.api.IFileTransmitServiceInstance;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.info.AreaMappingCategoryInfo;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public interface IAreaMappingServiceInstance extends IFileTransmitServiceInstance<AreaMappingInfo> {

	Collection<AreaMappingCategoryInfo> getMappingPathCategories();

	AreaMappingInfo getMappingByName(String mappingName) throws DronologyServiceException;

	GeneratedMappedArea generateAreaMapping(AreaMappingInfo info) throws DronologyServiceException;

	void executeAreaMapping(GeneratedMappedArea area) throws DronologyServiceException;

	GeneratedMappedArea generateAreaMapping(AreaMappingInfo info, Collection<IUAVProxy> selectedUAVs)
			throws DronologyServiceException;

}
