package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.fleet.DroneFleetManager;
import edu.nd.dronology.core.vehicle.ManagedDrone;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollisionAvoidanceCheckTask extends TimerTask {
    private static final ILogger LOGGER = LoggerProvider.getLogger(CollisionAvoidanceCheckTask.class);
    private final CollisionAvoider avoiderStrategy;
    private DroneCollector droneCollector;
    private final HashMap<String, DroneSnapshot> tmp = new HashMap<>();

    public CollisionAvoidanceCheckTask(CollisionAvoider avoiderStrategy) {
        this.avoiderStrategy = avoiderStrategy;
        this.droneCollector = new DroneCollector() {
            @Override
            public List<ManagedDrone> getManagedDrones() {
                return DroneFleetManager.getInstance().getRegisteredDrones();
//                return Collections.<ManagedDrone>emptyList();
            }
        };
    }

    @Override
    public void run() {
        LOGGER.debug("CA timer task started");
        List<ManagedDrone> allDrones = droneCollector.getManagedDrones().stream().filter(uav -> {
            // return uav.getFlightModeState().isInAir() || uav.getFlightModeState().isFlying();
            return uav.isStarted();
        }).collect(Collectors.toList());
        if (allDrones.size() < 1) {
            return;
        }
        ArrayBlockingQueue<DroneSnapshotOption> returnQueue = new ArrayBlockingQueue<>(allDrones.size());
        ArrayList<DroneSnapshot> input = new ArrayList<>();
        tmp.clear();
        for (ManagedDrone managedDrone : allDrones) {
            managedDrone.getMessenger().getSnapshot(returnQueue);
        }
        for (ManagedDrone managedDrone: allDrones) {
            try {
                DroneSnapshotOption snapshotOption = returnQueue.take();
                DroneSnapshot snapshot = snapshotOption.snapshot;
                if (Objects.nonNull(snapshot)) {
                    input.add(snapshot);
                    tmp.put(snapshot.getName(), snapshot);
                } else {
                    LOGGER.debug(snapshotOption.droneName + " didn't provide a snapshot.");
                }
            } catch (Exception e) {
                LOGGER.error(e);
                return;
            }
        }

        LOGGER.debug("Running avoid on " + input.size() + " drones");
        avoiderStrategy.avoid(input);

        for (ManagedDrone managedDrone : allDrones) {
            if (tmp.containsKey(managedDrone.getDroneName())) {
                managedDrone.getMessenger().updateGuidance(tmp.get(managedDrone.getDroneName()).getCommands());
            }
        }
    }

}
