package edu.nd.dronology.core.vehicle.manageddroneinternal.message;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

import java.util.concurrent.SynchronousQueue;

public class GetCoordinatesMessage extends AbstractMessage {
    public final SynchronousQueue<LlaCoordinate> returnBox = new SynchronousQueue<>();
}
