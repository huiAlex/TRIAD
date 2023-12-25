package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.backendmessage.*;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * The CollisionAvoidanceBackend is responsible for running the CollisionAvoider. Observes are registered in
 * ManagedDrone(s) and PhysicalDrone(s). These Observes pass along state. In the case of ManagedDrone, that state is the
 * set of active goals and the commandQueue. For PhysicalDrone, it's the drone's sensed state (its position, velocity
 * etc.). The data from both is combined to create DroneSnapshot(s) so that CollisionAvoider.avoid() can be called.
 * </p>
 *
 * <h2>implementation details (subject to change)</h2>
 * <p>
 * The idea behind CollisionAvoidanceBackend is to create an actor inspired object that only does work in response to
 * messages coming into its mailbox. CollisionAvoidanceBackend can do four things:
 * <ul>
 *     <li>Run CollisionAvoider.avoid() and disperse guidance to the managed drones. This class expects a stand-alone
 *     timer task to send this message periodically.</li>
 *
 *     <li>Receive an update message from a managed drone. This message includes a snapshot of the active goals as well
 *     as the queue of commands currently being executed. Messages of this type come in when the set of active goals
 *     changes or when the queue of commands changes. This happens when a goal or command is completed, for example.
 *     </li>
 *
 *     <li>Receive an update message from a physical drone. This message contains data sensed by a drones (its position,
 *     velocity, etc.). Messages of this type come in when new data is received from the drone.</li>
 *
 *     <li>Receive a stop command. This message should come in when the program is shutting down. This message causes
 *     the thread to gracefully stop.</li>
 * </ul>
 * </p>
 *
 *  <p>
 *     The {@link CollisionAvoidanceMessenger} object returned by {@link #getMessenger()} takes care of
 *     converting method calls into messages and delivering them to CollisionAvoidanceBackend's mailbox. All the
 *     functionality offered by this class is made accessible by the CollisionAvoidanceMessenger.
 * </p>
 * @see CollisionAvoidanceMessenger
 */
public class CollisionAvoidanceBackend implements Runnable {
    private static final ILogger LOGGER = LoggerProvider.getLogger(CollisionAvoidanceBackend.class);
    private static final boolean FIFO_MAILBOX = true;
    // TODO get this value, MAX_UAVS, from a configuration file
    private static final int MAX_UAVS = 10;
    // TODO get this value, UAV_REPORTS_PER_SECOND, from a configuration file
    private static final int UAV_REPORTS_PER_SECOND = 1;
    private static final int MAILBOX_BUFFER_FACTOR = 2;
    private static final int MAILBOX_CAPACITY = MAX_UAVS * UAV_REPORTS_PER_SECOND * MAILBOX_BUFFER_FACTOR;

    private final ArrayBlockingQueue<AbstractMessage> mailbox = new ArrayBlockingQueue<>(MAILBOX_CAPACITY, FIFO_MAILBOX);
    private final CollisionAvoider avoiderStrategy;
    private final CollisionAvoidanceMessenger messenger;
    private final Map<String, PhysicalDroneUpdateMessage> droneStates = new HashMap<>();
    private final Map<String, ManagedDroneUpdateMessage> droneControls = new HashMap<>();

    private boolean running = true;

    public CollisionAvoidanceBackend(CollisionAvoider avoiderStrategy) {
        this.avoiderStrategy = avoiderStrategy;
        this.messenger = new CollisionAvoidanceMessenger(this.mailbox);
    }

    public CollisionAvoidanceMessenger getMessenger() {
        return this.messenger;
    }

    private void stop() {
        running = false;
    }

    public void run() {
        int timeoutCount = 0;
        while(running) {
            try {
                AbstractMessage message = mailbox.poll(1000, TimeUnit.MILLISECONDS);

                if (message == null) {
                    timeoutCount = timeoutCount + 1;
                    if (timeoutCount > 2) {
                        LOGGER.warn("has not received any messages for " + timeoutCount + " seconds");
                    }
                } else {
                    timeoutCount = 0;
                    if (message instanceof  PhysicalDroneUpdateMessage) {
                        updatePhysicalDrone((PhysicalDroneUpdateMessage) message);
                    } else if (message instanceof  ManagedDroneUpdateMessage) {
                        updateManagedDrone((ManagedDroneUpdateMessage) message);
                    } else if (message instanceof CheckerTaskMessage) {
                        runAvoid();
                    } else if (message instanceof StopMessage) {
                        stop();
                    } else {
                        LOGGER.warn("UNKNOWN MESSAGE TYPE " + message);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.warn("CollisionAvoidanceBackend interrupted");
                LOGGER.trace(e);
            }
        }
    }

    private void updatePhysicalDrone(PhysicalDroneUpdateMessage msg) {
        droneStates.put(msg.name, msg);
    }

    private void updateManagedDrone(ManagedDroneUpdateMessage msg) {
        droneControls.put(msg.name, msg);

    }

    private void runAvoid() {
        ArrayList<DroneSnapshot> droneSnapshots = makeDroneSnapshots();
        avoiderStrategy.avoid(droneSnapshots);
        updateManagedDroneGuidance(droneSnapshots);
    }

    private ArrayList<DroneSnapshot> makeDroneSnapshots() {
        ArrayList<DroneSnapshot> snapshots = new ArrayList<>(droneStates.size());
        List<String> droneNames = droneStates.keySet().stream()
                .filter(droneName -> droneStates.containsKey(droneName) && droneControls.containsKey(droneName))
                .collect(Collectors.toList());
        for(String droneName : droneNames) {
            PhysicalDroneUpdateMessage droneState = droneStates.get(droneName);
            ManagedDroneUpdateMessage droneControl = droneControls.get(droneName);
            DroneSnapshotInternal snapshot = new DroneSnapshotInternal(droneName,
                    droneState.position, droneState.velocity, droneState.attitude);
            snapshot.getCommands().addAll(droneControl.commandQueue);
            snapshot.setState(droneControl.state);
            snapshot.setGoals(droneControl.goals);
            snapshots.add(snapshot);
        }
        return snapshots;
    }

    private void updateManagedDroneGuidance(ArrayList<DroneSnapshot> snapshots) {
        for (DroneSnapshot snapshot : snapshots) {
            droneControls.get(snapshot.getName()).managedDrone.getMessenger().updateGuidance(snapshot.getCommands());
        }
    }
}
