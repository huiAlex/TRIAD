package edu.nd.dronology.core.vehicle.commands;

public class ConnectionResponseCommand extends AbstractDroneCommand implements IDroneCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2039989878208633422L;

	public ConnectionResponseCommand(String groundstationId, boolean success) {
		super(groundstationId, CommandIds.CONNECTION_RESPONSE);
		data.put(ATTRIBUTE_SUCCESS, Boolean.toString(success));
	}

}
