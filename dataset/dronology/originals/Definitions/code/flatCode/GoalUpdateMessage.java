package edu.nd.dronology.core.vehicle.manageddroneinternal.message; 
import edu.nd.dronology.core.goal.AbstractGoal;

public class GoalUpdateMessage extends AbstractMessage {
    public final AbstractGoal goal;
    public GoalUpdateMessage(AbstractGoal goal) {
        this.goal = goal;
    }
}