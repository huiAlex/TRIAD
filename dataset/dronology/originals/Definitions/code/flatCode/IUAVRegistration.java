package edu.nd.dronology.services.core.items;

import java.io.Serializable;

public interface IUAVRegistration extends IPersistableItem {

	String getDescription();

	void setType(String type);

	void setDescription(String description);

	String getType();

	Serializable getAttribute(String key);

	void addAttribute(String key, Serializable value);

	void setUAVImage(byte[] image);

	byte[] getImage();

}
