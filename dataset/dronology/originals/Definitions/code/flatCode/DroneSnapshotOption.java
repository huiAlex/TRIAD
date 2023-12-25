package edu.nd.dronology.core.collisionavoidance;

public class DroneSnapshotOption {
    public final DroneSnapshot snapshot;
    public final String droneName;
    public DroneSnapshotOption(DroneSnapshot snapshot, String droneName) {
        this.snapshot = snapshot;
        this.droneName = droneName;
    }
}
