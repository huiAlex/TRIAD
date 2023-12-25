package edu.nd.dronology.core.vehicle.manageddroneinternal.message;

import java.util.concurrent.SynchronousQueue;

public class GetNameMessage extends AbstractMessage {
    public final SynchronousQueue<String> returnBox = new SynchronousQueue<>();
}
