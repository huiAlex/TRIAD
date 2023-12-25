package edu.nd.dronology.services.instances.registration.uavs;

import edu.nd.dronology.services.core.base.AbstractFileTransmitServerService;
import edu.nd.dronology.services.core.info.UAVRegistrationInfo;

public class UAVRegistrationService
		extends AbstractFileTransmitServerService<IUAVRegistrationServiceInstance, UAVRegistrationInfo> {

	private static volatile UAVRegistrationService INSTANCE;

	protected UAVRegistrationService() {
		super();
	}

	/**
	 * @return The singleton ConfigurationService instance
	 */
	public static UAVRegistrationService getInstance() {
		if (INSTANCE == null) {
			synchronized (UAVRegistrationService.class) {
				if (INSTANCE == null) {
					INSTANCE = new UAVRegistrationService();
				}
			}
		}
		return INSTANCE;

	}

	@Override
	protected IUAVRegistrationServiceInstance initServiceInstance() {
		return new UAVRegistrationServiceInstance();
	}



}
