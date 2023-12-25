package edu.nd.dronology.services.instances.registration.types;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.UAVTypeRegistrationInfo;

public class UAVTypeRegistrationService
		extends AbstractFileTransmitServerService<IUAVTypeRegistrationServiceInstance, UAVTypeRegistrationInfo> {

	private static volatile UAVTypeRegistrationService INSTANCE;

	protected UAVTypeRegistrationService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static UAVTypeRegistrationService getInstance() {
		if (INSTANCE == null) {
			synchronized (UAVTypeRegistrationService.class) {
				if (INSTANCE == null) {
					INSTANCE = new UAVTypeRegistrationService();
				}
			}
		}
		return INSTANCE;

	}

	@Override
	protected IUAVTypeRegistrationServiceInstance initServiceInstance() {
		return new UAVTypeRegistrationServiceInstance();
	}



}
