package edu.nd.dronology.services.core.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TagList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3805379647109731039L;
	private List<String> tags = new ArrayList<>();

	public void add(String tag) {
		tags .add(tag);

	}

}
