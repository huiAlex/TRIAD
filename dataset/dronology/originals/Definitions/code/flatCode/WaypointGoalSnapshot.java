package edu.nd.dronology.core.goal;

import edu.nd.dronology.core.coordinate.AbstractPosition;

public class WaypointGoalSnapshot implements IGoalSnapshot {
    private final AbstractPosition position;
    private final double speed;
    private final AbstractGoal.GoalState state;

    public WaypointGoalSnapshot(AbstractPosition position, double speed, AbstractGoal.GoalState state) {
        this.position = position;
        this.speed = speed;
        this.state = state;
    }

    @Override
    public AbstractGoal.GoalState getState() {
        return state;
    }

    public AbstractPosition getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }
}
