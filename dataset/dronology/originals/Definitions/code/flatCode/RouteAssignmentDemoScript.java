package edu.nd.dronology.misc.scripts;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.remote.IRemoteManager;

public class RouteAssignmentDemoScript {

	private static final String ADDRESS_SCHEME = "rmi://%s:%s/Remote";

	public static void main(String[] args) {
		try {

			IRemoteManager manager = (IRemoteManager) Naming.lookup(String.format(ADDRESS_SCHEME, "127.0.0.1", 9779));

			IFlightManagerRemoteService managerService = (IFlightManagerRemoteService) manager
					.getService(IFlightManagerRemoteService.class);

			List<Waypoint> route1 = new ArrayList<>();
			double lat = -43.123;
			double lon= 23.4123;
			double alt = 10;
			route1.add(new Waypoint(new LlaCoordinate(lat, lon, alt)));

			managerService.planFlight("UAV1", "TestRoute1", route1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
