package edu.nd.dronology.core.vehicle.manageddroneinternal;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.NedCommand;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.StopCommand;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand;
import edu.nd.dronology.core.vehicle.IDrone;
import edu.nd.dronology.core.vehicle.ManagedDrone;

/**
 * Managed Drones create an instance of this factory when they are constructed. This factory takes collision avoidance
 * commands and and makes executors that are specially configured to carryout the commands.
 */
public class CommandExecutorFactory {
    private IDrone drone;

    /**
     * Create a factory
     * @param drone the drone that executors act upon
     */
    public CommandExecutorFactory(IDrone drone) {
        this.drone = drone;
    }

    public CommandExecutor makeExecutor(Command cmd) {
        if (cmd instanceof WaypointCommand) {
            return new WaypointExecutor(this.drone, (WaypointCommand) cmd);
        }
        if (cmd instanceof StopCommand) {
            return new StopExecutor(this.drone, (StopCommand) cmd);
        }
        if (cmd instanceof NedCommand) {
            return new NedExecutor(this.drone, (NedCommand) cmd);
        }
        throw new IllegalArgumentException();
    }
}
