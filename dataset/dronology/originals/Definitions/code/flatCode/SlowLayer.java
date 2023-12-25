package edu.nd.dronology.core.collisionavoidance.strategy.onionbackend;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.guidancecommands.WaypointCommand;
import edu.nd.dronology.core.collisionavoidance.strategy.StopEveryone;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.goal.WaypointGoal;
import edu.nd.dronology.core.goal.WaypointGoalSnapshot;

public class SlowLayer implements ILayer {
    private final double speed;
    private final double distance;

    public SlowLayer(double triggerDistance, double speed)  {
        this.speed = speed;
        this.distance = triggerDistance;
    }

	@Override
	public void applyAction(DroneSnapshot snapshot) {
        WaypointGoalSnapshot wpGoal = StopEveryone.findActiveWaypointGoal(snapshot.getGoals());
        if (wpGoal != null) {
            double cmdSpeed = Math.min(wpGoal.getSpeed(), this.speed);
            flyToTargetAtSpeedIfNotAlready(snapshot, wpGoal.getPosition().toLlaCoordinate(), cmdSpeed);
        }
	}

	@Override
	public boolean isTriggered(double distance) {
		return distance < this.distance;
	}

	@Override
	public double getTriggerDistance() {
		return distance;
    }
    
    private void flyToTargetAtSpeedIfNotAlready(DroneSnapshot drone, LlaCoordinate target, double speed) {
        if (drone.getCommands().size() == 1) {
            if (drone.getCommands().get(0) instanceof WaypointCommand) {
                WaypointCommand wp = (WaypointCommand) drone.getCommands().get(0);
                boolean sameDest = wp.getDestination().toLlaCoordinate().equals(target);
                boolean sameSpeed = wp.getSpeed() == speed;
                if (sameDest && sameSpeed) {
                    return;
                }
            }
        }
        drone.getCommands().clear();
        WaypointCommand cmd = new WaypointCommand(target, speed);
        drone.getCommands().add(cmd);
    }

}