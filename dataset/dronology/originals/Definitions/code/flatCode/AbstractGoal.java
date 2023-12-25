package edu.nd.dronology.core.goal;

import java.util.Observable;
import java.util.Observer;




public abstract class AbstractGoal extends Observable implements Observer {

    public enum GoalState {
        PENDING,
        ACTIVE,
        COMPLETE,
        CANCELED
    }

    private GoalState state = GoalState.PENDING;

    public synchronized GoalState getState() {
        return state;
    }

    private synchronized void setState(GoalState newState) {
        this.state = newState;
        setChanged();
        notifyObservers();
    }

    public abstract IGoalSnapshot buildSnapshot();

    public void setPending() {
        setState(GoalState.PENDING);
    }

    public void setActive() {
        setState(GoalState.ACTIVE);
    }

    public void setCompleted() {
        setState(GoalState.COMPLETE);
    }

    public void setCanceled() {
        setState(GoalState.CANCELED);
    }
}
