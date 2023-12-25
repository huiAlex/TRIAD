package edu.nd.dronology.core.collisionavoidance.strategy.onionbackend;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.strategy.StopEveryone;
import edu.nd.dronology.core.goal.WaypointGoalSnapshot;

public class DefaultAction implements IAction {

	@Override
	public void applyAction(DroneSnapshot snapshot) {
        WaypointGoalSnapshot goal = StopEveryone.findActiveWaypointGoal(snapshot.getGoals());
        if (goal != null) {
            StopEveryone.flyToGoalIfNotAlready(snapshot, goal);
        }
	}

}