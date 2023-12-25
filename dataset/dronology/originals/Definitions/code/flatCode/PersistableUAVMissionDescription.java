package edu.nd.dronology.services.extensions.missionplanning.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.nd.dronology.services.core.items.IUAVMissionDescription;
import edu.nd.dronology.services.core.items.IUAVMissionTask;

public class PersistableUAVMissionDescription implements Serializable, IUAVMissionDescription {

	private static final long serialVersionUID = 1278076975080384731L;
	private String name;
	private String description;
	private Map<String, Serializable> attributes = new HashMap<>();
	private List<IUAVMissionTask> tasks = new ArrayList<>();

	public PersistableUAVMissionDescription(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;

	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addAttribute(String key, Serializable value) {
		attributes.put(key, value);
	}

	@Override
	public Serializable getAttributes(String key) {
		return attributes.get(key);
	}

	@Override
	public void addTask(IUAVMissionTask task) {
		tasks.add(task);
	}

	@Override
	public void addTask(IUAVMissionTask task, int index) {
		tasks.add(index, task);
	}

	@Override
	public boolean removeTask(int index) {
		if (index >= tasks.size()) {
			return false;
		}
		return tasks.remove(index) != null;
	}

	@Override
	public boolean removeTask(IUAVMissionTask task) {
		return tasks.remove(task);
	}

	@Override
	public List<IUAVMissionTask> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

}
