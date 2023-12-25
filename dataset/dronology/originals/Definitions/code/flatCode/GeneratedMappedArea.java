package edu.nd.dronology.services.core.areamapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneratedMappedArea implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5991442648634740620L;

	List<ExportDrone> uavList = new ArrayList<>();

	public void addUAVRouteAssignment(ExportDrone ed) {
		uavList.add(ed);

	}

	public List<ExportDrone> getUAVAssignments() {
		return Collections.unmodifiableList(uavList);
	}

}
