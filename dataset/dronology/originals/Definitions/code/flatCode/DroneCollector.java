package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.vehicle.ManagedDrone;

import java.util.List;

/**
 * I think of this as an agent who's one job is to find a list of ManagedDrones. The motivation for this to exist comes
 * from making it easier to isolate CollisionAvoidanceCheckTask under test.
 */
public interface DroneCollector {
    List<ManagedDrone> getManagedDrones();
}
