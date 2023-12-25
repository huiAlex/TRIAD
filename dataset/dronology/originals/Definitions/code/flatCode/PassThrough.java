package edu.nd.dronology.core.collisionavoidance.strategy;

import java.util.ArrayList;

import edu.nd.dronology.core.collisionavoidance.CollisionAvoider;
import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.goal.WaypointGoalSnapshot;

public class PassThrough implements CollisionAvoider {

	@Override
	public void avoid(ArrayList<DroneSnapshot> drones) {
        ArrayList<DroneSnapshot> flyingDrones = StopEveryone.findFlyingDrones(drones);
        for (DroneSnapshot drone : flyingDrones) {
            WaypointGoalSnapshot waypointGoal = StopEveryone.findActiveWaypointGoal(drone.getGoals());
            if (waypointGoal != null) {
                StopEveryone.flyToGoalIfNotAlready(drone, waypointGoal);
            }
        }
    }
    
}