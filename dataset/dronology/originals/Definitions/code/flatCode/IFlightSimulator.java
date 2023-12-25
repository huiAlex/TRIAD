package edu.nd.dronology.core.simulator;

import edu.nd.dronology.core.vehicle.internal.VirtualDrone;

/**
 * 
 * Interface for the internal simulator used with {@link VirtualDrone}.<br>
 * The simulator consists of a {@link IBatterySimulator} and a {@link IMovementSimulator} approximating the behavior of a UAV.
 * 
 * @author Michael Vierhauser
 *
 */
public interface IFlightSimulator extends IBatterySimulator, IMovementSimulator {

}
