package edu.nd.dronology.services.extensions.missionplanning.service.internal;

import edu.nd.dronology.core.status.IDronologyChangeListener;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class DronologySeriviceListener implements IDronologyChangeListener {

	@Override
	public void notifyUAVRemoved(String uavid) {
		try {
			MissionPlanningService.getInstance().removeUAV(uavid);
		} catch (DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void notifyGCSShutdown(String groundstationid) {
		try {
			MissionPlanningService.getInstance().cancelMission();
		} catch (DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
