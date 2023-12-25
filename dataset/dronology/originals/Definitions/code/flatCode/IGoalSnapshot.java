package edu.nd.dronology.core.goal;

/**
 * Read-only snapshot of a goal.
 */
public interface IGoalSnapshot {
    public abstract AbstractGoal.GoalState getState();
}
