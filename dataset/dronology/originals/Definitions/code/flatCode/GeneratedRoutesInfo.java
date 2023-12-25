package edu.nd.dronology.services.extensions.areamapping.selection;

import java.awt.geom.Path2D.Double;
import java.util.List;

import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;

public class GeneratedRoutesInfo {

	private final double avgLatitude;
	private final List<RoutePrimitive> routePrimitives;
	private final Double totalRiverSegment;
	private final List<RiverBank> bankList;

	public GeneratedRoutesInfo(List<RoutePrimitive> routePrimitives, double avgLatitude, Double totalRiverSegment,
			List<RiverBank> bankList) {
		super();
		this.avgLatitude = avgLatitude;
		this.routePrimitives = routePrimitives;
		this.totalRiverSegment = totalRiverSegment;
		this.bankList = bankList;
	}

	public List<RoutePrimitive> getRoutePrimitives() {
		return routePrimitives;
	}

	public double getAverageLatitude() {
		return avgLatitude;
	}

	public Double getTotalRiverSegment() {
		return totalRiverSegment;
	}

	public List<RiverBank> getBankList() {
		return bankList;
	}

}
