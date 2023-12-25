package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.backendmessage.*;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.goal.IGoalSnapshot;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * The CollisionAvoidanceMessenger class facilitates sending messages to instances of CollisionAvoidanceBackend. Use
 * instances of this class when you need to send a message to CollisionAvoidanceBackend.
 */
public class CollisionAvoidanceMessenger {
    private static final ILogger LOGGER = LoggerProvider.getLogger(CollisionAvoidanceMessenger.class);
    private final ArrayBlockingQueue<AbstractMessage> mailbox;

    // This constructor intentionally has no access modifier. Instances of this class should be created by
    // CollisionAvoidanceBackend.
    CollisionAvoidanceMessenger(ArrayBlockingQueue<AbstractMessage> mailbox) {
        this.mailbox = mailbox;
    }

    private void offerMessage(AbstractMessage msg) {
        if(!mailbox.offer(msg)) {
            LOGGER.warn("Could not add message to mailbox " + msg);
        }
    }

    /**
     * Send a message to the CollisionAvoidanceBackend telling it to run CollisionAvoider.avoid() and disperse guidance
     * to the managed drones.
     */
    //TODO rename this method
    public void sendCheckTask() {
        offerMessage(new CheckerTaskMessage());
    }

    /**
     * Send an update message with state from a physical drone. This message contains data sensed by a drones
     * (its position, velocity, etc.). Messages of this type should be sent when new data is received from the
     * drone.
     * @param snapshot a snapshot of the data received from the drone
     */
    public void sendPhysicalDroneUpdate(DroneSnapshotInternal snapshot) {
        offerMessage(new PhysicalDroneUpdateMessage(snapshot.getName(),
                snapshot.getPosition(), snapshot.getVelocity(), snapshot.getAttitude()));
    }

    /**
     * Sends an update message with state from a managed drone. This message contains a snapshot of the active goals as
     * well as the queue of commands currently being executed. Messages of this type should be sent when the set of
     * active goals changes or when the queue of commands changes.
     *
     * @param name the name of the drone
     * @param commandQueue the current queue of commands the managed drone is executing (this should not be the same
     *                     object use by managed drone, but a copy of that list)
     * @param goals a snapshot of the goals for this drone
     * @param state the state of the drone (FLYING, ON_GROUND, etc.)
     * @param managedDrone a reference to the managed drone this data came from (used to pass guidance back to the
     *                     managed drone after the next call of CollisionAvoider.avoid()).
     */
    public void sendManagedDroneUpdate(String name, List<Command> commandQueue, Set<IGoalSnapshot> goals, String state, ManagedDrone managedDrone) {
        offerMessage(new ManagedDroneUpdateMessage(name, commandQueue, goals, state, managedDrone));
    }

    /**
     * Send a stop message. This message should be sent when the program is shutting down. This message causes the
     * CollisionAvoidanceBackend thread to stop gracefully.
     */
    public void sendStopMessage() {
        try {
            mailbox.put(new StopMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Could not send stop command to CollisionAvoidanceBackend");
            LOGGER.trace(e);
            throw new RuntimeException(e);
        }

    }
 }
