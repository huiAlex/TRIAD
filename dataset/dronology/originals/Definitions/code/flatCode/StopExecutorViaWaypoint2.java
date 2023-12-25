package edu.nd.dronology.core.vehicle.manageddroneinternal;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshotInternal;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/*
This variation pick a waypoint some number of meters behind the UAV
*/
public class StopExecutorViaWaypoint2 implements CommandExecutor {

    private static final ILogger LOGGER = LoggerProvider.getLogger(StopExecutorViaWaypoint2.class);

    enum StopExecutionState {
        RADIOING,
        WATCHING_THE_CLOCK,
        WAITING_FOREVER,
        FINISHED;
    }

    private class UavData {
        public LlaCoordinate position;
        public Vector3D velocity;
        public double speed;
    }

    // Number of meters to set the waypoint away in the opposite direction of velocity
    private static final double WAYPOINT_DISTANCE = 20.0;

    private IDrone drone;
    StopExecutionState state = StopExecutionState.RADIOING;
    StopCommand data;
    long startTime;

    public StopExecutorViaWaypoint2(IDrone drone, StopCommand data) {
        this.drone = drone;
        this.data = data;
        LOGGER.debug("Creating stop executor for " + drone.getDroneName());
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
        LOGGER.debug("RADIOING STOP TO " + drone.getDroneName());
        UavData current = getUavData();
        Vector3D deltaPosition = current.velocity.normalize().scalarMultiply(-1.0 * WAYPOINT_DISTANCE);
        LlaCoordinate target = current.position.findLla(deltaPosition);
        drone.flyTo(target, current.speed);

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

    private UavData getUavData() {
        UavData result = new UavData();
        DroneSnapshotInternal currentState = drone.getLatestDroneSnapshot();
        result.position = currentState.getPosition();
        result.speed = Vector3D.distance(Vector3D.ZERO, currentState.getVelocity());
        result.velocity = currentState.getVelocity();
        return result;
    }

}
