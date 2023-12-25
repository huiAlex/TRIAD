package edu.nd.dronology.services.extensions.missionplanning.plan;

import edu.nd.dronology.services.extensions.missionplanning.MissionExecutionException;

public interface IExecuteableMissionPlan {

	boolean isMissionActive();

	void checkAndActivateTask() throws MissionExecutionException;

	void cancelMission();

}
