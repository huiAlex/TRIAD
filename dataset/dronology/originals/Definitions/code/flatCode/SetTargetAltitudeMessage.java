package edu.nd.dronology.core.vehicle.manageddroneinternal.message;

public class SetTargetAltitudeMessage extends AbstractMessage {
    public final double altitude;

    public SetTargetAltitudeMessage(double altitude) {
        this.altitude = altitude;
    }
}
