package edu.nd.dronology.core.collisionavoidance.strategy.onionbackend;

public interface ILayer extends IAction {
    /**
     * Given a distance, this method returns true if the layer should be triggered.
     */
    public boolean isTriggered(double distance);

    public double getTriggerDistance();

}