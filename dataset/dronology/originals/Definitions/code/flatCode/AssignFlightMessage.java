package edu.nd.dronology.core.vehicle.manageddroneinternal.message;

import edu.nd.dronology.core.flight.IFlightDirector;

public class AssignFlightMessage extends AbstractMessage {
    public final IFlightDirector flightDirective;

    public AssignFlightMessage(IFlightDirector flightDirective) {
        this.flightDirective = flightDirective;
    }
}
