package edu.nd.dronology.services.extensions.areamapping;

import java.util.Collection;
import java.util.List;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.areamapping.ExportAllocationInformation;
import edu.nd.dronology.services.core.areamapping.ExportDrone;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.extensions.areamapping.creation.MapRiver;
import edu.nd.dronology.services.extensions.areamapping.selection.GeneratedRoutesInfo;
import edu.nd.dronology.services.extensions.areamapping.selection.IRouteSelectionStrategy;
import edu.nd.dronology.services.extensions.areamapping.selection.RouteSelectionResult;
import edu.nd.dronology.services.extensions.areamapping.selection.StrategyFactory;
import edu.nd.dronology.services.extensions.areamapping.util.SelectionWriter;

public class AreaMappingGenerator {

	private IAreaMapping mapping;
	private Collection<IUAVProxy> selectedUAVs;
	private MapRiver riverMapper;

	public AreaMappingGenerator(IAreaMapping mapping, Collection<IUAVProxy> selectedUAVs) {
		this.mapping = mapping;
		this.selectedUAVs = selectedUAVs;

	}

	public GeneratedMappedArea generateMapping() throws DronologyServiceException {

		try {

			riverMapper = new MapRiver(mapping);

			IRouteSelectionStrategy selector = StrategyFactory.getSelectionStrategy();

			long startGenerate = System.currentTimeMillis();
			GeneratedRoutesInfo gInfo = new GeneratedRoutesInfo(riverMapper.generateRoutePrimitives(),
					riverMapper.getAverageLatitude(), riverMapper.getTotalRiverSegment(), riverMapper.getBankList());
			long endGenerate = System.currentTimeMillis();

			selector.initialize(gInfo, selectedUAVs, mapping);

			long startSelect = System.currentTimeMillis();
			RouteSelectionResult result = selector.generateAssignments();
			long endSelect = System.currentTimeMillis();

			System.out.println("ROUTE Generation: " + (endGenerate - startGenerate) / 1000);
			System.out.println("ROUTE Selection: " + (endSelect - startSelect) / 1000);
			System.out.println("Specs: ");
			ExportAllocationInformation flight = result.getEportAllocationInformation().get(0);
			System.out.println("Mission Score: " + flight.getMetricStatistics().getAllocationScore());
			System.out.println("Coverage: " + flight.getMetricStatistics().getAllocationCoverage());
			System.out.println("Equality of Tasks: " + flight.getMetricStatistics().getEqualityOfTasks());
			System.out.println("Collisions: " + flight.getMetricStatistics().getCollisions());

			List<ExportDrone> allocations = result.getEportAllocationInformation().get(0).getDroneAllocations();

			GeneratedMappedArea area = new GeneratedMappedArea();

			allocations.forEach(ed -> {
				area.addUAVRouteAssignment(ed);
			});

			new SelectionWriter().writeRouteSelection(result);

			return area;

		} catch (Throwable t) {
			t.printStackTrace();
			throw new DronologyServiceException(t.getMessage());
		}
	}
}
