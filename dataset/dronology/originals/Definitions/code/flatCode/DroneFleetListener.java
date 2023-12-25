package edu.nd.dronology.core.fleet;

import edu.nd.dronology.core.vehicle.ManagedDrone;

public interface DroneFleetListener {
    void droneAdded(ManagedDrone managedDrone);
    void droneRemoved(ManagedDrone managedDrone);
}