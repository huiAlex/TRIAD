package edu.nd.dronology.services.core.items;

import java.io.Serializable;

public interface IUAVTypeRegistration extends IPersistableItem {

	String getDescription();

	void setDescription(String description);

	Serializable getAttribute(String key);

	void addAttribute(String key, Serializable value);

	void setUAVImage(byte[] image);

	byte[] getImage();

}
