package edu.nd.dronology.core.collisionavoidance.strategy;

import edu.nd.dronology.core.collisionavoidance.CollisionAvoider;
import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand;
import edu.nd.dronology.core.goal.AbstractGoal;
import edu.nd.dronology.core.goal.IGoalSnapshot;
import edu.nd.dronology.core.goal.WaypointGoalSnapshot;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The StopEveryone CollisionAvoider is a failsafe that only triggers if it detects two drones are intruding into each
 * others space because something has gone wrong. Use this with mission plans that carefully navigate the drones to
 * avoid crashing into one another. StopEveryone assumes the drones will follow a mission plan that takes into account
 * where all the drones will be in space and time. When the StopEveryone CollisionAvoider is triggered the mission is
 * aborted, and humans need to land the drones manually.
 */
public class StopEveryone implements CollisionAvoider {
    private static final ILogger LOGGER = LoggerProvider.getLogger(StopEveryone.class);
    private final double threshold;

    /**
     * Initializes a newly created StopEveryone object that triggers all drones to stop whatever they're doing and hover
     * in place, if any two drones move closer than the threshold distance to one another.
     *
     * @param threshold distance in meters. If any two drones move close enough to be within the threshold distance,
     *                  then StopEveryone will command all drones to stop whatever they're doing and hover in place.
     */
    public  StopEveryone(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void avoid(ArrayList<DroneSnapshot> drones) {
        ArrayList<DroneSnapshot> flyingDrones = findFlyingDrones(drones);
        if (isSafe(flyingDrones)) {
            // fly to the goal
            for (DroneSnapshot drone : flyingDrones) {
                WaypointGoalSnapshot waypointGoal = findActiveWaypointGoal(drone.getGoals());
                if (waypointGoal != null) {
                    LOGGER.debug(drone.getName() + " had a waypoint goal: " + waypointGoal.getPosition().toLlaCoordinate());
                    flyToGoalIfNotAlready(drone, waypointGoal);
                } else {
                    LOGGER.debug(drone.getName() + " had no waypoint goal");
                }
            }
        }
        else {
            // stop everyone
            for (DroneSnapshot drone : flyingDrones) {
                LOGGER.fatal("WARNING ALL DRONES STOPPED");
                onStopTrigger(drone);
            }
        }
    }

    protected void onStopTrigger(DroneSnapshot drone) {
        stopDroneIfNotStopped(drone);
    }

    /**
     * @param flyingDrones the drones that are flying (not all drones)
     * @return true if every drone is at least threshold distance apart
     */
    private boolean isSafe(ArrayList<DroneSnapshot> flyingDrones) {
        for (int i = 0; i < flyingDrones.size() - 1; ++i) {
            for (int j = i + 1; j < flyingDrones.size(); ++j) {
                if (isTooClose(flyingDrones.get(i), flyingDrones.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Check if two drones are too close
     * @param a the first drone
     * @param b the second drone
     * @return true if the distance between the drones is less than the threshold distance
     */
    private boolean isTooClose(DroneSnapshot a, DroneSnapshot b) {
        double distance = a.getPosition().distance(b.getPosition());
        boolean result = distance < this.threshold;
        if (result) {
            LOGGER.warn("DRONES TOO CLOSE " + a.getName() + ", " + b.getName() + " distance: " + distance);
        }
        return result;
    }

    /**
     * given the drone snapshots, filter out the drones that are not flying
     * @param drones all the drone snapshots
     * @return a list with all the flying drones
     */
    public static ArrayList<DroneSnapshot> findFlyingDrones(ArrayList<DroneSnapshot> drones) {
        return drones.stream()
                .filter(drone -> "FLYING".equals(drone.getState()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // if no active waypoint goal exists return null, otherwise return the first one it finds. Note if more than one
    // active waypoint goal exists in the set of active goals, the active goal that is returned is up to the set's
    // iterator
    public static WaypointGoalSnapshot findActiveWaypointGoal(Set<IGoalSnapshot> goals) {
        for (IGoalSnapshot goal: goals) {
            if (goal instanceof WaypointGoalSnapshot && goal.getState() == AbstractGoal.GoalState.ACTIVE) {
                return (WaypointGoalSnapshot) goal;
            }
        }
        return null;
    }

    /**
     * Make sure the drone's command queue has a stop command, overwriting the current command queue if needed.
     * @param drone to stop
     */
    public static void stopDroneIfNotStopped(DroneSnapshot drone) {
        // we don't need to do anything if the first command in the command queue is a stop command
        if (!drone.getCommands().isEmpty()) {
            if (drone.getCommands().get(0) instanceof StopCommand) {
                return;
            }
        }
        drone.getCommands().clear();
        drone.getCommands().add(new StopCommand(-1.0));
    }

    /*
    if this method is called, we want the drone to fly to the current goal, so we will wipe out command queue and put a
    waypoint command in there unless the drone is already doing the right thing.
     */
    public static void flyToGoalIfNotAlready(DroneSnapshot drone, WaypointGoalSnapshot goal) {
        // check if we are doing the right thing: that we have one cmd in the command queue and that cmd matches the
        // current waypoint goal
        if (drone.getCommands().size() == 1) {
            if (drone.getCommands().get(0) instanceof WaypointCommand) {
                WaypointCommand wp = (WaypointCommand) drone.getCommands().get(0);
                boolean sameDest = wp.getDestination().toLlaCoordinate().equals(goal.getPosition().toLlaCoordinate());
                boolean sameSpeed = wp.getSpeed() == goal.getSpeed();
                if (sameDest && sameSpeed) {
                    return;
                }
            }
        }
        // we have now filtered out the case where we don't have to do anything. If this code runs, we need to replace
        // what's in the command queue
        drone.getCommands().clear();
        WaypointCommand cmd = new WaypointCommand(goal.getPosition().toLlaCoordinate(), goal.getSpeed());
        drone.getCommands().add(cmd);
    }
}
