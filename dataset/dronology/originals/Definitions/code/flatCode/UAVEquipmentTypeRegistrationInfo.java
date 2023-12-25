package edu.nd.dronology.services.core.info;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UAVEquipmentTypeRegistrationInfo extends RemoteInfoObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2844123024068335148L;
	private String type = "Default";
	private List<String> uavids;

	public UAVEquipmentTypeRegistrationInfo(String name, String id) {
		super(name, id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;

	}

}
