package edu.nd.dronology.services.core.info;

public class UAVTypeRegistrationInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2844123024068335148L;
	private String type = "Default";

	public UAVTypeRegistrationInfo(String name, String id) {
		super(name, id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;

	}

}
