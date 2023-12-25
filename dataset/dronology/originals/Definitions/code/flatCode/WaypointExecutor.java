package edu.nd.dronology.core.vehicle.manageddroneinternal;

import edu.nd.dronology.core.DronologyConstants;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;

public class WaypointExecutor implements CommandExecutor {

    enum WaypointExecutionState {
        RADIOING,
        SENSING,
        FINISHED;
    }

    private IDrone drone;
    WaypointExecutionState state = WaypointExecutionState.RADIOING;
    WaypointCommand data;

    public WaypointExecutor(IDrone drone, WaypointCommand data) {
        this.drone = drone;
        this.data = data;
    }

    @Override
    public void process() {
        switch (this.state) {
            case RADIOING:
                radioDrone();
                break;

            case SENSING:
                senseDrone();
                break;

            case FINISHED:
            default:
                // Do nothing.
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return this.state == WaypointExecutionState.FINISHED;
    }

    private void radioDrone() {
        drone.flyTo(this.data.getDestination(), this.data.getSpeed());
        this.state = WaypointExecutionState.SENSING;
    }

    private void senseDrone() {
        double targetDistance = drone.getCoordinates().distance(data.getDestination());
        if (targetDistance < DronologyConstants.THRESHOLD_WAYPOINT_DISTANCE) {
            this.state = WaypointExecutionState.FINISHED;
        }
    }
}
