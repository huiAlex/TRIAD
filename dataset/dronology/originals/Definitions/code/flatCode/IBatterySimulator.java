package edu.nd.dronology.core.simulator;

import edu.nd.dronology.core.vehicle.internal.VirtualDrone;

/**
 * 
 * The battery simulator simulates the battery usage / voltage drain while a {@link VirtualDrone} is in flight.
 * 
 * @author Michael Vierhauser
 *
 */
public interface IBatterySimulator {

	void startBatteryDrain();

	void stopBatteryDrain();

	double getVoltage();

}
