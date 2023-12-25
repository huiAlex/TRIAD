package edu.nd.dronology.core.collisionavoidance.strategy;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import edu.nd.dronology.core.collisionavoidance.CollisionAvoider;
import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.NedCommand;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.goal.WaypointGoalSnapshot;

public class FollowTheLeaderNed implements CollisionAvoider {

    private static final double THRESHOLD_DISTANCE = 1.0;
    private static final double FOLLOW_DISTANCE = 10.0;
    private static final Vector3D NED_OFFSET = new Vector3D(0, 0, -FOLLOW_DISTANCE);

	@Override
	public void avoid(ArrayList<DroneSnapshot> drones) {
        DroneSnapshot leader = findLeader(drones);
        DroneSnapshot follower = findFollower(drones);

        if (leader != null) {
            WaypointGoalSnapshot wp = StopEveryone.findActiveWaypointGoal(leader.getGoals());
            if (wp != null) {
                StopEveryone.flyToGoalIfNotAlready(leader, wp);
            }

            if (follower != null) {
                Vector3D ned = findFollowVelocity(leader.getPosition(), follower.getPosition());
                follower.getCommands().clear();
                follower.getCommands().add(new NedCommand(ned.getX(), ned.getY(), ned.getZ(), 2.0));
            }
        } else if (follower != null) {
            WaypointGoalSnapshot wp = StopEveryone.findActiveWaypointGoal(follower.getGoals());
            if (wp != null) {
                StopEveryone.flyToGoalIfNotAlready(follower, wp);
            }   
        }
    }

    Vector3D findFollowVelocity(LlaCoordinate leader, LlaCoordinate follower) {
        LlaCoordinate followPoint = leader.findLla(NED_OFFSET);
        double distance = followPoint.distance(follower);
        if (distance > THRESHOLD_DISTANCE) {    
            double speed = Math.min(distance, 7.0);
            Vector3D arrow = follower.findNed(followPoint);
            return arrow.normalize().scalarMultiply(speed);
        }
        return Vector3D.ZERO;
    }
    
    static DroneSnapshot findLeader(ArrayList<DroneSnapshot> drones) {
        for (DroneSnapshot drone: drones) {
            if ("LEADER".equals(drone.getName())) {
                return drone;
            }
        }
        return null;
    }

    static DroneSnapshot findFollower(ArrayList<DroneSnapshot> drones) {
        for (DroneSnapshot drone: drones) {
            if (!"LEADER".equals(drone.getName())) {
                return drone;
            }
        }
        return null;
     }
}