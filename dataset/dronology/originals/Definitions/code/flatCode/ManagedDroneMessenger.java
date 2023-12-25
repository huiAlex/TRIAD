package edu.nd.dronology.core.vehicle.manageddroneinternal;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.DroneSnapshotOption;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.flight.IFlightDirector;
import edu.nd.dronology.core.goal.AbstractGoal;
import edu.nd.dronology.core.vehicle.manageddroneinternal.message.*;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ManagedDroneMessenger {

    private static final ILogger LOGGER = LoggerProvider.getLogger(ManagedDroneMessenger.class);
    private final ArrayBlockingQueue<AbstractMessage> mailbox;

    public ManagedDroneMessenger(ArrayBlockingQueue<AbstractMessage> mailbox) {
        this.mailbox = mailbox;
    }

    private void offerMessage(AbstractMessage msg) {
        if(!mailbox.offer(msg)) {
            LOGGER.warn("Could not add message to mailbox " + msg);
        }
    }

    private <T> T returnService(AbstractMessage msg, SynchronousQueue<T> returnBox) {
        try {
            mailbox.put(msg);
            return returnBox.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * <p>
     *  Gets the current position of the drone.
     * </p>
     *
     * <p>
     *     Note this sends a message and blocks while it waits to receive a return message from the managed drone with
     *     the coordinates. The time it takes for this method to run is indeterminate.
     * </p>
     * @return
     */
    public LlaCoordinate getCoordinates() {
        GetCoordinatesMessage msg = new GetCoordinatesMessage();
        return returnService(msg, msg.returnBox);
    }

    /**
     * Assigns a flight directive to the managed drone
     *
     * @param flightDirective
     */
    public void assignFlight(IFlightDirector flightDirective) {
        AssignFlightMessage msg = new AssignFlightMessage(flightDirective);
        offerMessage(msg);
    }

    /**
     * Removes an assigned flight
     */
    public void unassignFlight() {
        UnassignFlightMessage msg = new UnassignFlightMessage();
        offerMessage(msg);
    }

    /**
     * Tell the drone to fly home
     */
    public void returnHome() {
        ReturnHomeMessage msg = new ReturnHomeMessage();
        offerMessage(msg);
    }

    /**
     *
     * @param targetAltitude
     *          Sets target altitude for takeoff
     */
    public void setTargetAltitude(double targetAltitude) {
        SetTargetAltitudeMessage msg = new SetTargetAltitudeMessage(targetAltitude);
        offerMessage(msg);
    }

    /**
     * Tell the drone to takeoff
     */
    public void takeOff() {
        TakeOffMessage msg = new TakeOffMessage();
        offerMessage(msg);
    }

//    /**
//     *
//     * Get the drone's unique ID.
//     *  <p>
//     *     Note this sends a message and blocks while it waits to receive a return message from the managed drone with
//     *     the return value. The time it takes for this method to run is indeterminate.
//     * </p>
//     * @return unique drone ID
//     */
//    public String getDroneName() {
//        GetNameMessage msg = new GetNameMessage();
//        return returnService(msg, msg.returnBox);
//    }

    /**
     * Tell the drone to land. The drone will touch down on the ground.
     *
     */
    public void land() {
        LandMessage msg = new LandMessage();
        offerMessage(msg);
    }

    /**
     * Get the drone's base coordinates (its home location).
     *  <p>
     *     Note this sends a message and blocks while it waits to receive a return message from the managed drone with
     *     the return value. The time it takes for this method to run is indeterminate.
     * </p>
     * @return the managed drone's base coordinates
     */
    public LlaCoordinate getBaseCoordinates() {
        GetCoordinatesMessage msg = new GetCoordinatesMessage();
        return returnService(msg, msg.returnBox);
    }

    /**
     * Update the ManagedDrones list of commands.
     * @param commands The commands this drone should carry out
     */
    public void updateGuidance(List<Command> commands) {
        UpdateGuidanceMessage msg = new UpdateGuidanceMessage(commands);
        offerMessage(msg);
    }

    public void getSnapshot(ArrayBlockingQueue<DroneSnapshotOption> returnQueue) {
        GetSnapshotMessage msg = new GetSnapshotMessage(returnQueue);
        offerMessage(msg);
    }

    public void updateGoal(AbstractGoal goal) {
        GoalUpdateMessage msg = new GoalUpdateMessage(goal);
        offerMessage(msg);
    }
}
