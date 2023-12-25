package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;

public class DronePair {
    public final DroneSnapshot a;
    public final DroneSnapshot b;

    public DronePair(DroneSnapshot a, DroneSnapshot b) {
        this.a = a;
        this.b = b;
    }

    public double findDistance() {
        return a.getPosition().distance(b.getPosition());
    }
}