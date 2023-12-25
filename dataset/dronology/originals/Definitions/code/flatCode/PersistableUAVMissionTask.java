package edu.nd.dronology.services.extensions.missionplanning.persistence;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.nd.dronology.services.core.items.IUAVMissionTask;

public class PersistableUAVMissionTask implements Serializable, IUAVMissionTask {
	
	public static final String DELAY_DURATION ="duration";
	

	private static final long serialVersionUID = 2287087726638295092L;
	private String id;
	private Map<String, Serializable> attributes = new HashMap<>();
	private String type;

	public PersistableUAVMissionTask(String id, String type) {
		this.id = id;
		this.setType(type);
	}

	public void addAttribute(String key, Serializable value) {
		attributes.put(key, value);
	}

	@Override
	public Serializable getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Map<String,Serializable> getParameters() {
		return Collections.unmodifiableMap(attributes);
	}

}
