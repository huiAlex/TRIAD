package edu.nd.dronology.core.vehicle.manageddroneinternal;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshotInternal;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;

public class StopExecutorViaWaypoint implements CommandExecutor {

    enum StopExecutionState {
        RADIOING,
        WATCHING_THE_CLOCK,
        WAITING_FOREVER,
        FINISHED;
    }

    private class PositionAndSpeed {
        public LlaCoordinate position;
        public double speed;
    }

    private IDrone drone;
    StopExecutionState state = StopExecutionState.RADIOING;
    StopCommand data;
    long startTime;

    public StopExecutorViaWaypoint(IDrone drone, StopCommand data) {
        this.drone = drone;
        this.data = data;
    }

    @Override
    public void process() {
        switch (state) {
            case RADIOING:
                radioDrone();
                break;

            case WATCHING_THE_CLOCK:
                checkClock();
                break;

            case WAITING_FOREVER:
            case FINISHED:
            default:
                // Do nothing.
                break;
        }
    }

    private void radioDrone() {
        PositionAndSpeed current = getCurrentPositionAndSpeed();
        drone.flyTo(current.position, current.speed);

        double waitDuration = data.getTime();
        if (waitDuration > 0.0) {
            startTime = System.nanoTime();
            state = StopExecutionState.WATCHING_THE_CLOCK;
        }
        else {
            state = StopExecutionState.WAITING_FOREVER;
        }
    }

    private void checkClock() {
        double waitDuration = data.getTime();
        long deltaNano = System.nanoTime()- startTime;
        double deltaT = deltaNano * 1.0e-9;
        if (deltaT > waitDuration) {
            state = StopExecutionState.FINISHED;
        }
    }

    @Override
    public boolean isFinished() {
        return state == StopExecutionState.FINISHED;
    }

    private PositionAndSpeed getCurrentPositionAndSpeed() {
        PositionAndSpeed result = new PositionAndSpeed();
        DroneSnapshotInternal currentState = drone.getLatestDroneSnapshot();
        result.position = currentState.getPosition();
        result.speed = Vector3D.distance(Vector3D.ZERO, currentState.getVelocity());
        return result;
    }

}
