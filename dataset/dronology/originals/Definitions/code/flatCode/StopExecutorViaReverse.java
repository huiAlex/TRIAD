package edu.nd.dronology.core.vehicle.manageddroneinternal;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshotInternal;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;

public class StopExecutorViaReverse implements CommandExecutor {

    enum StopExecutionState {
        RADIOING_REVERSE,
        WAITING_TO_REVERSE,
        RADIOING_STOP,
        WATCHING_THE_CLOCK,
        WAITING_FOREVER,
        FINISHED;
    }

    // seconds to wait after sending reverse NED command before sending NED(0,0,0) command
    private static final double REVERSAL_WAIT_TIME = 2.0;

    // how fast should the UAV reverse to stop
    private static final double REVERSAL_SPEED = 7.0;

    private IDrone drone;
    StopExecutionState state = StopExecutionState.RADIOING_REVERSE;
    StopCommand data;
    long reverseRadioTime;
    long startTime;

    public StopExecutorViaReverse(IDrone drone, StopCommand data) {
        this.drone = drone;
        this.data = data;
    }

    @Override
    public void process() {
        switch (state) {
            case RADIOING_REVERSE:
                radioReverse();
                break;
            case WAITING_TO_REVERSE:
                waitForReversal();
                break;
            case RADIOING_STOP:
                radioStop();
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

    private void radioReverse() {
        //should this flatten? what if the UAV is going up?
        Vector3D reverseNED = drone.getLatestDroneSnapshot().getVelocity().scalarMultiply(-1.0).normalize().scalarMultiply(REVERSAL_SPEED);
        drone.setVelocity(reverseNED.getX(), reverseNED.getY(), reverseNED.getZ());
        reverseRadioTime = System.nanoTime();
        state = StopExecutionState.WAITING_TO_REVERSE;
    }

    private void waitForReversal() {
        double reversalWaitTimer = (System.nanoTime() - reverseRadioTime) * 1.0e-9;
        if (REVERSAL_WAIT_TIME < reversalWaitTimer) {
            state = StopExecutionState.RADIOING_STOP;
        }
    }

    private void radioStop() {
        drone.setVelocity(0.0, 0.0, 0.0);

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

}
