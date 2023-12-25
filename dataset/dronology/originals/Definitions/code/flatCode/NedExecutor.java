package edu.nd.dronology.core.vehicle.manageddroneinternal;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.NedCommand;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;

public class NedExecutor implements CommandExecutor {

    enum NedExecutionState {
        RADIOING,
        WATCHING_THE_CLOCK,
        FINISHED;
    }

    private IDrone drone;
    NedExecutionState state = NedExecutionState.RADIOING;
    NedCommand data;
    long startTime;

    public NedExecutor(IDrone drone, NedCommand data) {
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
            case FINISHED:
            default:
                // Do nothing.
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return state == NedExecutionState.FINISHED;
    }

    private void radioDrone() {
        drone.setVelocity(data.getNorth(), data.getEast(), data.getDown());
        startTime = System.nanoTime();
        state = NedExecutionState.WATCHING_THE_CLOCK;
    }

    private void checkClock() {
        double waitDuration = data.getTime();
        long deltaNano = System.nanoTime()- startTime;
        double deltaT = deltaNano * 1.0e-9;
        if (deltaT > waitDuration) {
            state = NedExecutionState.FINISHED;
        }
    }
}
