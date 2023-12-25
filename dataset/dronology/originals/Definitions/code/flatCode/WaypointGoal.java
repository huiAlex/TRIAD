package edu.nd.dronology.core.goal;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.coordinate.AbstractPosition;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.core.vehicle.internal.PhysicalDrone;

import java.util.Observable;


public class WaypointGoal extends AbstractGoal {

    // The distance threshold (meters) for determining when a waypoint has been reached
    private double dThresh = 2.0;

    private final AbstractPosition position;
    private final double speed;

    public WaypointGoal(Waypoint waypoint) {
        this.position = waypoint.getCoordinate();
        this.speed = waypoint.getApproachingspeed();
    }

    public WaypointGoal(AbstractPosition position, double speed) {
        this.position = position;
        this.speed = speed;
    }

    public WaypointGoal(Waypoint waypoint, double dThresh) {
        this.position = waypoint.getCoordinate();
        this.speed = waypoint.getApproachingspeed();
        this.dThresh = dThresh;
    }


    public AbstractPosition getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public void update(Observable observable, Object o) {
        GoalState curState = getState();

        if (observable instanceof PhysicalDrone) {
            if (curState == GoalState.ACTIVE) {
                DroneSnapshot snapshot = ((PhysicalDrone) observable).getLatestDroneSnapshot();
                double dist = position.distance(snapshot.getPosition());

                if (dist <= dThresh) {
                    setCompleted();
                }
            }
        }
        else if (observable instanceof AbstractGoal) {
            if (curState == GoalState.PENDING) {
                GoalState state = ((AbstractGoal) observable).getState();

                if (state == GoalState.COMPLETE) {
                    setActive();
                }
            }
        }
    }

    @Override
    public IGoalSnapshot buildSnapshot() {
        return new WaypointGoalSnapshot(this.getPosition(), this.getSpeed(), this.getState());
    }
}
