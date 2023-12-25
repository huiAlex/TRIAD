package edu.nd.dronology.services.extensions.areamapping.selection;

import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.services.core.areamapping.ExportAllocationInformation;

public class RouteSelectionResult {
	private List<ExportAllocationInformation> allocationInfo;
	private long selectionTime; // in milliseconds

	public RouteSelectionResult() {
		allocationInfo = new ArrayList<>();
	}

	public List<ExportAllocationInformation> getEportAllocationInformation() {
		return allocationInfo;
	}

	public double getSelectionTime() {
		return selectionTime;
	}

	// public void setExportAllocationInformation(List<ExportAllocationInformation> info) {
	// allocationInfo = info;
	// }

	public void setSelectionTime(long time) {
		selectionTime = time;
	}

	public void add(ExportAllocationInformation ass) {
		allocationInfo.add(ass);

	}
}
