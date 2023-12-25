package edu.nd.dronology.core.collisionavoidance.strategy.onionbackend;

import edu.nd.dronology.core.collisionavoidance.DroneSnapshot;
import edu.nd.dronology.core.collisionavoidance.strategy.StopEveryone;

public class StopLayer implements ILayer {
    private final double triggerDistance;

    public StopLayer(double triggerDistance) {
        this.triggerDistance = triggerDistance;
    }

	@Override
	public void applyAction(DroneSnapshot snapshot) {
		StopEveryone.stopDroneIfNotStopped(snapshot);
	}

	@Override
	public boolean isTriggered(double distance) {
		return distance < this.triggerDistance;
	}

	@Override
	public double getTriggerDistance() {
		return triggerDistance;
	}
}