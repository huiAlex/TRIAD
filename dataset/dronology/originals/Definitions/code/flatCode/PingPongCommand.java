package edu.nd.dronology.core.vehicle.commands;

import edu.nd.dronology.core.CoordinateChange;

@CoordinateChange
public class PingPongCommand extends AbstractDroneCommand implements IDroneCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4365284928942347912L;

	public PingPongCommand(String uavid) {
		super(uavid, CommandIds.PING_PONG_COMMAND);
	}

}
