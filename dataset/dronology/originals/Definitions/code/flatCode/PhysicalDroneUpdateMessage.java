package edu.nd.dronology.core.collisionavoidance.backendmessage;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class PhysicalDroneUpdateMessage extends AbstractMessage {
    public String name;
    public final LlaCoordinate position;
    public final Vector3D velocity;
    public final Vector3D attitude;
    public PhysicalDroneUpdateMessage(String name, LlaCoordinate position, Vector3D velocity, Vector3D attitude) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.attitude = attitude;
    }
}
