package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.vehicle.ManagedDrone;

import java.util.Collections;
import java.util.List;

public class DefaultDroneCollector implements DroneCollector {
    @Override
    public List<ManagedDrone> getManagedDrones() {
        return Collections.<ManagedDrone>emptyList();
    }
}
