package edu.nd.dronology.services.core.items;

import java.io.Serializable;
import java.util.List;

public interface IUAVMissionDescription {

	String getName();

	List<IUAVMissionTask> getTasks();

	String getDescription();

	void setDescription(String description);

	void setName(String name);

	void addAttribute(String key, Serializable value);

	Serializable getAttributes(String key);

	void addTask(IUAVMissionTask task);

	void addTask(IUAVMissionTask task, int index);

	boolean removeTask(int index);

	boolean removeTask(IUAVMissionTask task);

}
