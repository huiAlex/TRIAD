package edu.nd.dronology.services.extensions.missionplanning.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.nd.dronology.services.core.items.IMissionPlan;
import edu.nd.dronology.services.core.items.IUAVMissionDescription;

public class PersistableMissionPlan implements IMissionPlan {

	private String name;
	private String id;
	private String description;
	private Map<String, Serializable> attributes = new HashMap<>();
	private List<IUAVMissionDescription> missionDescriptions = new ArrayList<>();

	public PersistableMissionPlan() {
		id = UUID.randomUUID().toString();
		name = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;

	}

	public void addAttribute(String key, Serializable value) {
		attributes.put(key, value);
	}

	public Serializable getAttributes(String key) {
		return attributes.get(key);
	}

	@Override
	public List<IUAVMissionDescription> getMissionDescriptions() {
		return Collections.unmodifiableList(missionDescriptions);
	}

	@Override
	public void addMissionDescription(IUAVMissionDescription description) {
		missionDescriptions.add(description);
	}

	public boolean removeMissionDescription(String name) {
		for (IUAVMissionDescription desc : new ArrayList<>(missionDescriptions)) {
			if (desc.getName().equals(name)) {
				missionDescriptions.remove(desc);
				return true;
			}

		}
		return false;
	}

}
