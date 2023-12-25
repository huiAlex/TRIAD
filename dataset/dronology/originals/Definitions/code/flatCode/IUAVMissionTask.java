package edu.nd.dronology.services.core.items;

import java.io.Serializable;
import java.util.Map;

public interface IUAVMissionTask {

	String getType();

	String getId();

	Serializable getAttribute(String key);

	Map<String, Serializable> getParameters();


}
