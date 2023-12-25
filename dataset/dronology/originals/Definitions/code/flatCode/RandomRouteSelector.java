package edu.nd.dronology.services.extensions.areamapping.selection.random;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.areamapping.MetricsStatistics;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.extensions.areamapping.internal.Geometry;
import edu.nd.dronology.services.extensions.areamapping.metrics.AllocationInformation;
import edu.nd.dronology.services.extensions.areamapping.metrics.Drone;
import edu.nd.dronology.services.extensions.areamapping.metrics.MetricsRunner;
import edu.nd.dronology.services.extensions.areamapping.metrics.MetricsUtilities;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.selection.GeneratedRoutesInfo;
import edu.nd.dronology.services.extensions.areamapping.selection.IRouteSelectionStrategy;
import edu.nd.dronology.services.extensions.areamapping.selection.ResultCreationUtil;
import edu.nd.dronology.services.extensions.areamapping.selection.RouteSelectionResult;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;

public class RandomRouteSelector implements IRouteSelectionStrategy {

	private static final double APERATURE_WIDTH = 10;
	private static final double APERATURE_HEIGHT = 0.8 * APERATURE_WIDTH;
	private static final double OVERLAP_FACTOR = 0.7;
	private static final int DEFAULT_RESULTS = 100;
	private double avgLatitude;
	// private int availableDrones;
	private List<RoutePrimitive> routePrimitives;
	private MetricsRunner metricsRunner;
	private List<IUAVProxy> uavs;
	private long start;
	private long end;
	private List<AllocationInformation> allAllocations;

	@Override
	public void initialize(GeneratedRoutesInfo info, Collection<IUAVProxy> uavs, IAreaMapping mapping)
			throws DronologyServiceException {

		try {
			routePrimitives = Utilities.splitRoutePrimitives(info.getRoutePrimitives(), APERATURE_HEIGHT, OVERLAP_FACTOR);
			this.avgLatitude = info.getAverageLatitude();
			this.uavs = new ArrayList<>(uavs);
			metricsRunner = new MetricsRunner(routePrimitives, info.getTotalRiverSegment(), info.getBankList(),
					APERATURE_WIDTH, APERATURE_HEIGHT, uavs.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DronologyServiceException(e.getMessage());
		}

	}

	@Override
	public RouteSelectionResult generateAssignments(int numAssignments) throws DronologyServiceException {
		try {
			allAllocations = new ArrayList<>();

			IntStream.range(0, numAssignments).forEach(i -> createAssignment());

			return createResult();
		} catch (Throwable t) {
			t.printStackTrace();
			throw new DronologyServiceException(t.getMessage());
		}

	}

	private void createAssignment() {
		List<Drone> assignments = generateRandomAssingments();
		AllocationInformation currentAllocation = new AllocationInformation();
		currentAllocation.setDroneAllocations(assignments);
		currentAllocation.setMetricsStatistics(generateMetricsStatistics(assignments));
		allAllocations.add(currentAllocation);

	}

	private RouteSelectionResult createResult() {
		Collections.sort(allAllocations);
		ResultCreationUtil rcUtil = new ResultCreationUtil(routePrimitives);
		rcUtil.createRoutes(avgLatitude);

		RouteSelectionResult result = rcUtil.createResult(allAllocations);
		result.setSelectionTime(end - start);

		return result;

	}

	// GeneratedRouteAssignment....
	private List<Drone> generateRandomAssingments() {
		try {
		Set<Integer> assignedRoutes = new HashSet<>();
		int availableDrones = uavs.size();
		int droneNum;
		int routeNum;

		int routeAssignmentNum = MetricsUtilities.generateRandomNumber(routePrimitives.size() - 1, 1);
		List<Drone> droneList = new ArrayList<>();
		for (int i = 0; i < availableDrones; i++) {
			droneList.add(new Drone());
			LlaCoordinate home = uavs.get(i).getHomeLocation();
			LlaCoordinate currentLocation = uavs.get(i).getCoordinates();
			droneList.get(i).setDroneHomeLocation(
					Geometry.gpsToCartesian(new Point2D.Double(home.getLatitude(), home.getLongitude()), avgLatitude));
			droneList.get(i).setDroneStartPoint(Geometry.gpsToCartesian(
					new Point2D.Double(currentLocation.getLatitude(), currentLocation.getLongitude()), avgLatitude));
			droneList.get(i).setUAVId(uavs.get(i).getID());
		}

		// while (assignedRoutes.size() < routeAssignmentNum) {
		// // assign drone routes in here
		// droneNum = MetricsUtilities.generateRandomNumber(availableDrones - 1, 0);
		// routeNum = MetricsUtilities.generateRandomNumber(routeAssignmentNum, 0);
		// while (assignedRoutes.contains(routeNum)) {
		// routeNum = MetricsUtilities.generateRandomNumber(routeAssignmentNum, 0);
		// }
		// droneList.get(droneNum).getDroneRouteAssignment().add(routePrimitives.get(routeNum));
		// assignedRoutes.add(routeNum);
		// }
		List<RoutePrimitive> shuffledRoutes = new ArrayList<>(routePrimitives);
		Collections.shuffle(shuffledRoutes);
		List<RoutePrimitive> availableRoutes = new ArrayList(shuffledRoutes.subList(0, (routeAssignmentNum+1)));
		AtomicInteger assignCounter = new AtomicInteger(0);
		while (assignCounter.get() < routeAssignmentNum) {
			droneNum = MetricsUtilities.generateRandomNumber(availableDrones - 1, 0);
			routeNum = MetricsUtilities.generateRandomNumber(availableRoutes.size()-1, 0);
			RoutePrimitive toAssign = availableRoutes.remove(routeNum);
			droneList.get(droneNum).getDroneRouteAssignment().add(toAssign);
			assignCounter.incrementAndGet();
		}
		return droneList;
		}catch (Throwable e) {
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
	}

	private MetricsStatistics generateMetricsStatistics(List<Drone> drones) {
		long start = System.currentTimeMillis();
		metricsRunner.setDroneAssignments(drones);
		MetricsStatistics metrics = metricsRunner.runMetrics();
		long end  = System.currentTimeMillis();
//		System.out.println(end-start);
		return metrics;
	}

	@Override
	public RouteSelectionResult generateAssignments() throws DronologyServiceException {
		return generateAssignments(DEFAULT_RESULTS);
	}

}
