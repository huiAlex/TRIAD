package edu.nd.dronology.services.extensions.areamapping.unused;
//package edu.nd.dronology.services.extensions.areamapping;
//
//import java.util.Collection;
//import java.util.logging.Logger;
//
//import edu.nd.dronology.core.vehicle.IUAVProxy;
//import edu.nd.dronology.services.core.areamapping.ExportAllocationInformation;
//import edu.nd.dronology.services.core.items.IAreaMapping;
//import edu.nd.dronology.services.core.util.DronologyServiceException;
//import edu.nd.dronology.services.extensions.areamapping.creation.IRouteCreator;
//import edu.nd.dronology.services.extensions.areamapping.creation.MapRiver;
//import edu.nd.dronology.services.extensions.areamapping.selection.GeneratedRoutesInfo;
//import edu.nd.dronology.services.extensions.areamapping.selection.IRouteSelectionStrategy;
//import edu.nd.dronology.services.extensions.areamapping.selection.RouteSelectionResult;
//
//public class RouteCreationRunner {
//
//	private IRouteCreator riverMapper;
//
//	public RouteSelectionResult run(IAreaMapping mapping, Collection<IUAVProxy> uavs) {
//
////		riverMapper = new MapRiver(mapping);
////
////		IRouteSelectionStrategy selector = StrategyFactory.getSelectionStrategy();
////
////		long startGenerate = System.currentTimeMillis();
////		GeneratedRoutesInfo gInfo = new GeneratedRoutesInfo(riverMapper.generateRoutePrimitives(),
////				riverMapper.getAverageLatitude(), riverMapper.getTotalRiverSegment(), riverMapper.getBankList());
////		long endGenerate = System.currentTimeMillis();
////
////		try {
////			selector.initialize(gInfo, uavs, mapping);
////
////			long startSelect = System.currentTimeMillis();
////			RouteSelectionResult createdRouteAssignments = selector.generateAssignments();
////			long endSelect = System.currentTimeMillis();
////
////			System.out.println("ROUTE Generation: " + (endGenerate - startGenerate) / 1000);
////			System.out.println("ROUTE Selection: " + (endSelect - startSelect) / 1000);
////			System.out.println("Specs: ");
////			ExportAllocationInformation flight = createdRouteAssignments.getEportAllocationInformation().get(0);
////			System.out.println("Mission Score: " + flight.getMetricStatistics().getAllocationScore());
////			System.out.println("Coverage: " + flight.getMetricStatistics().getAllocationCoverage());
////			System.out.println("Equality of Tasks: " + flight.getMetricStatistics().getEqualityOfTasks());
////			System.out.println("Collisions: " + flight.getMetricStatistics().getCollisions());
////			return createdRouteAssignments;
////		} catch (Throwable e) {
////			e.printStackTrace();
////			System.out.println(e);
////		}
////		/// fix throw Exception...
////		return new RouteSelectionResult();
////
////	}
//
//}
