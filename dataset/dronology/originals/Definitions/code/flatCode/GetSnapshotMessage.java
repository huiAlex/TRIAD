package edu.nd.dronology.core.vehicle.manageddroneinternal.message;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.DroneSnapshotOption;

import java.util.concurrent.ArrayBlockingQueue;

public class GetSnapshotMessage extends AbstractMessage {
    public final ArrayBlockingQueue<DroneSnapshotOption> returnBox;
    public GetSnapshotMessage(ArrayBlockingQueue<DroneSnapshotOption> returnQueue) {
        this.returnBox = returnQueue;
    }
}
