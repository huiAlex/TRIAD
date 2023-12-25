package edu.nd.dronology.services.extensions.areamapping.instances;

import java.util.Collection;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.AreaMappingCategoryInfo;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.info.FlightRouteCategoryInfo;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * 
 * 
 * 
 * 
 * @author Michael Vierhauser
 *
 */
public class AreaMappingService
		extends AbstractFileTransmitServerService<IAreaMappingServiceInstance, AreaMappingInfo> {

	private static volatile AreaMappingService INSTANCE;

	protected AreaMappingService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static AreaMappingService getInstance() {
		if (INSTANCE == null) {
			synchronized (AreaMappingService.class) {
				if (INSTANCE == null) {
					INSTANCE = new AreaMappingService();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected IAreaMappingServiceInstance initServiceInstance() {
		return new AreaMappingServiceInstance();
	}

	public Collection<AreaMappingCategoryInfo> getMappingPathCategories() {
		return serviceInstance.getMappingPathCategories();
	}

	public AreaMappingInfo getItem(String name) throws DronologyServiceException {
		return serviceInstance.getItem(name);
	}

	public AreaMappingInfo getMappingByName(String mappingName) throws DronologyServiceException {
		return serviceInstance.getMappingByName(mappingName);
	}

	public GeneratedMappedArea generateAreaMapping(AreaMappingInfo info) throws DronologyServiceException {
		return serviceInstance.generateAreaMapping(info);
	}

	public GeneratedMappedArea generateAreaMapping(AreaMappingInfo info, Collection<IUAVProxy> selectedUAVs)
			throws DronologyServiceException {
		return serviceInstance.generateAreaMapping(info, selectedUAVs);
	}

	public void executeAreaMapping(GeneratedMappedArea area) throws DronologyServiceException {
		serviceInstance.executeAreaMapping(area);
	}
}
