package edu.nd.dronology.services.extensions.missionplanning.service.internal;

import edu.nd.dronology.services.core.items.IMissionPlan;
import edu.nd.dronology.services.extensions.missionplanning.persistence.PersistableMissionPlan;

public class MissionElementFactory {

	public static IMissionPlan createNewMissionPlan() {
		return new PersistableMissionPlan();
	}

}
