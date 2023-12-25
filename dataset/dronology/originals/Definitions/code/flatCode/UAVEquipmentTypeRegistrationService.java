package edu.nd.dronology.services.instances.registration.equipment;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;

import edu.nd.dronology.services.core.info.UAVEquipmentTypeRegistrationInfo;

public class UAVEquipmentTypeRegistrationService
		extends AbstractFileTransmitServerService<IUAVEquipmentTypeRegistrationServiceInstance, UAVEquipmentTypeRegistrationInfo> {

	private static volatile UAVEquipmentTypeRegistrationService INSTANCE;

	protected UAVEquipmentTypeRegistrationService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static UAVEquipmentTypeRegistrationService getInstance() {
		if (INSTANCE == null) {
			synchronized (UAVEquipmentTypeRegistrationService.class) {
				if (INSTANCE == null) {
					INSTANCE = new UAVEquipmentTypeRegistrationService();
				}
			}
		}
		return INSTANCE;

	}

	@Override
	protected IUAVEquipmentTypeRegistrationServiceInstance initServiceInstance() {
		return new UAVEquipmentTypeRegistrationServiceInstance();
	}



}
