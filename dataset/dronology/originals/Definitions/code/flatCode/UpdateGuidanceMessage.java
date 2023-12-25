package edu.nd.dronology.core.vehicle.manageddroneinternal.message;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;

import java.util.List;

public class UpdateGuidanceMessage extends AbstractMessage {
    public final List<Command> commands;
    public UpdateGuidanceMessage(List<Command> commands) {
        this.commands = commands;
    }
}
