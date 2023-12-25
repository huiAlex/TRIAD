package edu.nd.dronology.services.extensions.areamapping.creation;

import com.google.gson.FieldNamingPolicy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.geom.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.rmi.CORBA.Util;
import javax.xml.transform.Source;

import org.apache.commons.collections4.Get;

import java.rmi.Naming;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.extensions.areamapping.internal.AdaptedCreepingLinePrimitive;
import edu.nd.dronology.services.extensions.areamapping.internal.Geometry;
import edu.nd.dronology.services.extensions.areamapping.internal.PriorityPolygonPrimitive;
import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;
import edu.nd.dronology.services.extensions.areamapping.internal.RiverBankPrimitive;
import edu.nd.dronology.services.extensions.areamapping.internal.RiverList;
import edu.nd.dronology.services.extensions.areamapping.internal.SearchPatternStrategy;
import edu.nd.dronology.services.extensions.areamapping.internal.SourcePoints;
import edu.nd.dronology.services.extensions.areamapping.metrics.MetricsRunner;
import edu.nd.dronology.services.extensions.areamapping.model.RiverSubsegment;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive.RouteType;
import edu.nd.dronology.services.extensions.areamapping.util.Utilities;
import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.items.IMappedItem;
import edu.nd.dronology.services.core.items.PriorityArea;


public class MapRiver implements IRouteCreator{
	/*
	 * ASSUMPTIONS: APERATURE_WIDTH shall be measured in meters. MAX_RIVER_WIDTH
	 * assumed to be 1000 meters.
	 */
	final static double OVERLAP_FACTOR = 0.7;
	final static double APERATURE_WIDTH = 10;
	final static double APERATURE_HEIGHT = APERATURE_WIDTH*0.8;
	final static double MAX_RIVER_WIDTH = 10000;
	private static int dronesNum = 10;
	private static final String ADDRESS_SCHEME = "rmi://%s:%s/Remote";
	private Vector<Double> latitudeBounds;
	private List<RiverBank> bankList;
	private Path2D.Double totalRiverSegment;
	private RiverList bankMapping;
	private List<SourcePoints> priorityAreas;

	@Deprecated
	public MapRiver() {
		parseJSON();
		latitudeBounds = new Vector<>();
		preprocessRiverBanks(bankMapping);
	}
	
	public MapRiver(IAreaMapping mapping) {
		latitudeBounds = new Vector<>();
		priorityAreas = new ArrayList<>();
		bankList = Utilities.processDronologyInput(Utilities.edgeLlaToRiverBank(mapping.getMappedPoints(0)), Utilities.edgeLlaToRiverBank(mapping.getMappedPoints(1)), latitudeBounds);
		Utilities.verifyInputOrder(bankList.get(0), bankList.get(1));
		List<IMappedItem> prioritySpots = mapping.getLocationMappings();
		for(IMappedItem area : prioritySpots) {
			priorityAreas.add(Utilities.transformPriorityArea(area, getAverageLatitude()));
		}
	}
	
	@Override
	public List<RiverBank> getBankList(){
		return bankList;
	}
	
	@Override
	public Path2D.Double getTotalRiverSegment(){
		return totalRiverSegment;
	}
	
	@Override
	public double getAverageLatitude() {
		return Utilities.getAvgLatitude(latitudeBounds.get(0), latitudeBounds.get(1));
	}
	
	/**
	 * This method uses the GSON library to automatically parse the riverbank JSON file
	 */
	private void parseJSON() {
		Gson GSON = new GsonBuilder().enableComplexMapKeySerialization()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).serializeSpecialFloatingPointValues()
				.create();

		try {
			// Importing JSON file
			List<String> jsonString = java.nio.file.Files.readAllLines(Paths.get(System.getProperty("user.dir")
					+ "/src/edu/nd/dronology/services/extensions/areamapping/json/riverSegment1.json"));
			StringBuilder sb = new StringBuilder();
			for (String s : jsonString) {
				sb.append(s);
			}
			// Automatic parsing of JSON file with GSON library
			bankMapping = GSON.fromJson(sb.toString(), RiverList.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*private void parseAreaMapping(AreaMapping mappedRiverSegment) {
		
	}*/
	
	/**
	 * This method preprocesses the riverbank nodes to ensure that they are in the necessary order for the route generation algorithms.
	 * @param bankMapping
	 * @return river banks in the required order
	 */
	private void preprocessRiverBanks(RiverList bankMapping) {
		bankList = Utilities.makeBankVectors(bankMapping, latitudeBounds);
		Utilities.verifyInputOrder(bankList.get(0), bankList.get(1));
	}
	
	// Connect to dronology and send search routes to drones
	/**
	 * This method exports the routes to the drones
	 * @param routes
	 */
	public void exportRoutes(List<RoutePrimitive> routes) {
		for (int i = 0; i < routes.size(); i++) {
			routes.set(i, Utilities.cartesianRouteToGpsRoute(routes.get(i), Utilities.getAvgLatitude(latitudeBounds.get(0), latitudeBounds.get(1))));
		}
		try {
			IRemoteManager manager = (IRemoteManager) Naming
					.lookup(String.format(ADDRESS_SCHEME, "127.0.0.1", 9779));
			IFlightManagerRemoteService managerService = (IFlightManagerRemoteService) manager
					.getService(IFlightManagerRemoteService.class);
			List<List<Waypoint>> waypointList = new ArrayList<>();
			for (RoutePrimitive rlist : routes) {
				List<Waypoint> route1 = new ArrayList<>();
				for (Point2D.Double entry : rlist.getRoute()) {
					route1.add(new Waypoint(new LlaCoordinate(entry.getX(), entry.getY(), 10)));
				}
				waypointList.add(route1);
			}
			System.out.println("route1: " + waypointList.get(0));
			managerService.planFlight("VRTL0", "TestRoute1", waypointList.get(0));
			//managerService.planFlight("VRTL1", "TestRoute2", waypointList.get(1));
			// managerService.planFlight("ND-3", "TestRoute3", waypointList.get(2));
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	/**
	 * This method generates a variety of route primitive options for selection.
	 * @return route primitives
	 */
	@Override
	public List<RoutePrimitive> generateRoutePrimitives() {
			// Generate search route primitives
			List<SourcePoints> sourcePoints = new ArrayList<>();
			List<RoutePrimitive> routes = new ArrayList<>();

			
			//just for getting home location for drones in metrics runner
			System.out.println(bankList.get(0).get(0) + ", " + bankList.get(0).get(1) + ", " + bankList.get(0).get(2));
			
			//used for the printout
			Vector<RiverSubsegment> riverVectors = new Vector<>();
			riverVectors.add(new RiverSubsegment(bankList.get(0),bankList.get(1)));
			
			sourcePoints = Geometry.generateSourcePoints(APERATURE_WIDTH, APERATURE_HEIGHT, OVERLAP_FACTOR, bankList, false);
			SearchPatternStrategy bankRoutes = new RiverBankPrimitive();
			bankRoutes.setSourcePoints(sourcePoints);
			bankRoutes.setRouteType(RouteType.RIVERBANK);
			routes.addAll(bankRoutes.generateRoutePrimitive(APERATURE_HEIGHT, OVERLAP_FACTOR));
			Utilities.debugPrintOut(riverVectors, routes);
			
			SearchPatternStrategy creepingLine = new AdaptedCreepingLinePrimitive();
			creepingLine.setSourcePoints(sourcePoints);
			creepingLine.setRouteType(RouteType.CRISSCROSS);
			routes.addAll(creepingLine.generateRoutePrimitive(APERATURE_HEIGHT, OVERLAP_FACTOR));
			
			sourcePoints = Geometry.generateSourcePoints(APERATURE_WIDTH, APERATURE_HEIGHT, OVERLAP_FACTOR, bankList, true);
			SearchPatternStrategy innerCreepingLine = new AdaptedCreepingLinePrimitive();
			innerCreepingLine.setSourcePoints(sourcePoints);
			innerCreepingLine.setRouteType(RouteType.INNER_CRISSCROSS);
			routes.addAll(innerCreepingLine.generateRoutePrimitive(APERATURE_HEIGHT, OVERLAP_FACTOR));
			
			Utilities.debugPrintOut(riverVectors, routes);
			
			totalRiverSegment = Utilities.makeTotalRiverSegment(bankList);

			//routes = Utilities.splitRoutePrimitives(routes, dronesNum, APERATURE_HEIGHT, OVERLAP_FACTOR);
			Utilities.debugPrintOut(riverVectors, routes);
			
			
			SearchPatternStrategy priorityArea = new PriorityPolygonPrimitive();
			//priorityArea.generateRoutePrimitive(APERATURE_HEIGHT, OVERLAP_FACTOR);
			priorityArea.setSourcePoints(priorityAreas);
			priorityArea.setRouteType(RouteType.PRIORITYAREA);
			routes.addAll(priorityArea.generateRoutePrimitive(APERATURE_HEIGHT, OVERLAP_FACTOR));
			for(RoutePrimitive routePrimitive : routes) {
				System.out.println("route type: " + routePrimitive.getRouteType());
			}
			return routes;
	}

	public static void main(String[] args) {
		MapRiver runRoute = new MapRiver();
		List<RoutePrimitive> routes = runRoute.generateRoutePrimitives();
		//MetricsRunner metricsRunner = new MetricsRunner(routes, runRoute.totalRiverSegment, runRoute.bankList, APERATURE_WIDTH, APERATURE_HEIGHT);
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < 1; i ++) {
			//metricsRunner.runMetrics();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("That took " + (endTime - startTime) + " milliseconds");
		//runRoute.exportRoutes(routes);
	}

}