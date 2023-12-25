package edu.nd.dronology.core.collisionavoidance.backendmessage;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.goal.IGoalSnapshot;
import edu.nd.dronology.core.vehicle.ManagedDrone;

import java.util.List;
import java.util.Set;

public class ManagedDroneUpdateMessage extends AbstractMessage {
    public final String name;
    public final List<Command> commandQueue;
    public final Set<IGoalSnapshot> goals;
    public final String state;
    public final ManagedDrone managedDrone;
    public ManagedDroneUpdateMessage(String name, List<Command> commandQueue, Set<IGoalSnapshot> goals, String state, ManagedDrone managedDrone) {
        this.name = name;
        this.commandQueue = commandQueue;
        this.goals = goals;
        this.state = state;
        this.managedDrone = managedDrone;
    }
}
