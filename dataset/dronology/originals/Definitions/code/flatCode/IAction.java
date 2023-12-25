package edu.nd.dronology.core.collisionavoidance.strategy.onionbackend;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;

public interface IAction {
    public void applyAction(DroneSnapshot snapshot);
}