package edu.nd.dronology.core.collisionavoidance.guidancecommands;

import edu.nd.dronology.core.coordinate.LlaCoordinate;

/**
 * A command that tells a UAV to fly to a specific place.
 */
public class WaypointCommand extends Command {

    private final LlaCoordinate destination;
    private final double speed;

    /**
     * <p>
     * Creates a command that tells the UAV to fly to the given coordinates on Earth.
     * </p>
     * <p>
     * For example, to command a UAV to fly to the Notre Dame Stadium (10m above the middle of the 50-yard line)
     * <pre>
     *         {@code
     *         WaypointCommand nd = new WaypointCommand(new LlaCoordinate(41.698394, -86.233923, 236.0))
     *         }
     *     </pre>
     * </p>
     *
     * @param destination the coordinates where the UAV should fly.
     */
    public WaypointCommand(LlaCoordinate destination, double speed) {
        this.destination = destination;
        this.speed = speed;
    }

    /**
     * @return destination coordinates. The place where the UAV should go.
     */
    public LlaCoordinate getDestination() {
        return destination;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "WaypointCommand(lat:" + destination.getLatitude() + ", long:" + destination.getLongitude() + ", alt:" +
                destination.getAltitude() + ", speed:" + speed + ")";
    }
}
