package edu.nd.dronology.core.collisionavoidance.strategy;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand;

public class StopEveryoneWaypoint extends StopEveryone {

    public static final double STOP_SPEED = 2.0;

    public StopEveryoneWaypoint(double threshold) {
        super(threshold);
    }

    @Override
    protected void onStopTrigger(DroneSnapshot drone) {
        drone.getCommands().clear();
        drone.getCommands().add(new WaypointCommand(drone.getPosition(), STOP_SPEED));
    }

}