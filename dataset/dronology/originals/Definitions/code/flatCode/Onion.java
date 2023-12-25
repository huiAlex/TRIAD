package edu.nd.dronology.core.collisionavoidance.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import edu.nd.dronology.core.collisionavoidance.CollisionAvoidanceUtil;
import edu.nd.dronology.core.collisionavoidance.CollisionAvoider;
import edu.nd.dronology.core.collisionavoidance.DronePair;
import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.strategy.onionbackend.DefaultAction;
import edu.nd.dronology.core.collisionavoidance.strategy.onionbackend.IAction;
import edu.nd.dronology.core.collisionavoidance.strategy.onionbackend.ILayer;
import edu.nd.dronology.core.goal.IGoalSnapshot;
import edu.nd.dronology.core.goal.WaypointGoalSnapshot;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

/**
 * This approach uses a collection of layers to determine avoidance actions. The inner most layer stops the drones. The next layer out might slow them. 
 */
public class Onion implements CollisionAvoider {

    private static final ILogger LOGGER = LoggerProvider.getLogger(Onion.class);

    // List of layers, ordered inner most to outer most. 
    List<ILayer> layers;
    HashMap<String, DroneSnapshot> nameLookup = new HashMap<>();
    HashMap<String, Double> distanceLookup = new HashMap<>();
    HashMap<String, String> nearestNeighbor = new HashMap<>();
    final IAction defaultAction = new DefaultAction();

    public Onion(ILayer...layers) {
        this.layers = new ArrayList<>(Arrays.asList(layers));
        Collections.sort(this.layers, (ILayer l1, ILayer l2) -> {
            return Double.compare(l1.getTriggerDistance(), l2.getTriggerDistance());
        });
    }

	@Override
	public void avoid(ArrayList<DroneSnapshot> drones) {
        setupLookupTables(drones);
        for (DronePair pair : CollisionAvoidanceUtil.findPairs(drones)) {
            double distance = pair.findDistance();
            if (!distanceLookup.containsKey(pair.a.getName()) || 
                distance < distanceLookup.get(pair.a.getName())) {    
                
                distanceLookup.put(pair.a.getName(), distance);
                nearestNeighbor.put(pair.a.getName(), pair.b.getName());

            }
            if (!distanceLookup.containsKey(pair.b.getName()) ||
                distance < distanceLookup.get(pair.b.getName())) {
                
                distanceLookup.put(pair.b.getName(), distance);
                nearestNeighbor.put(pair.b.getName(), pair.a.getName());
            }
        }

        for (DroneSnapshot drone : drones) {
            IAction activeLayer = defaultAction;
            if (distanceLookup.size() != 0) {
                double distance = distanceLookup.get(drone.getName());
                ILayer layer = findInnerMostLayer(distance);
                if (layer != null) {
                    activeLayer = layer;
                    String neighbor = nearestNeighbor.get(drone.getName());
                    String msg = String.format("Collision avoidance is taking action. %s is %.2f meters from %s", drone.getName(), distance, neighbor);
                    LOGGER.warn(msg);
                }
            }
            activeLayer.applyAction(drone);
        }
    }

    private void setupLookupTables(ArrayList<DroneSnapshot> drones) {
        nameLookup.clear();
        distanceLookup.clear();
        nearestNeighbor.clear();
        for (DroneSnapshot drone : drones) {
            nameLookup.put(drone.getName(), drone);
        }
    }
    
    private ILayer findInnerMostLayer(double distance) {
        for (ILayer layer: layers) {
            if (layer.isTriggered(distance)) {
                return layer;
            }
        }
        return null;
    }

}