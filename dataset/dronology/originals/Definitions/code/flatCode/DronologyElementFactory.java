package edu.nd.dronology.services.extensions.areamapping.instances;

import edu.nd.dronology.services.core.items.AreaMapping;
import edu.nd.dronology.services.core.items.IAreaMapping;

public class DronologyElementFactory {

	

	public static IAreaMapping createNewAreaMapping() {
		return new AreaMapping();
	}


}
