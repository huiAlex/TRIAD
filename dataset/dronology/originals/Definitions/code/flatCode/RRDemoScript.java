package edu.nd.dronology.misc.scripts;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.remote.IFlightRouteplanningRemoteService;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class RRDemoScript {

	private static final String ADDRESS_SCHEME = "rmi://%s:%s/Remote";

	public static void main(String[] args) {
		try {

			IRemoteManager manager = (IRemoteManager) Naming.lookup(String.format(ADDRESS_SCHEME, "127.0.0.1", 9779));

			// IDroneSetupRemoteService service = (IDroneSetupRemoteService)
			// manager.getService(IDroneSetupRemoteService.class);
			//
			IFlightManagerRemoteService managerService = (IFlightManagerRemoteService) manager
					.getService(IFlightManagerRemoteService.class);

			IFlightRouteplanningRemoteService planningService = (IFlightRouteplanningRemoteService) manager
					.getService(IFlightRouteplanningRemoteService.class);
			//

			List<FlightRouteInfo> allRoutes = new ArrayList<>(planningService.getItems());

			for (FlightRouteInfo r : allRoutes) {
				System.out.println(r.getName());

			}

			FlightRouteInfo r1 = getRouteByName("RR2_SouthArea", allRoutes);
			FlightRouteInfo r2 = getRouteByName("RR3A_RiverBank", allRoutes);
			FlightRouteInfo r3 = getRouteByName("RR3B_RiverBank", allRoutes);
			FlightRouteInfo r4 = getRouteByName("RR4A_RiverBank2", allRoutes);
			FlightRouteInfo r5 = getRouteByName("RR4B_RiverBank2", allRoutes);

			if (r1 == null) {
				System.out.println("RR2_SouthArea not found!!!");
			}
			if (r2 == null) {
				System.out.println("RR3A_RiverBank not found!!!");
			}
			if (r3 == null) {
				System.out.println("RR3B_RiverBank not found!!!");
			}
			if (r4 == null) {
				System.out.println("RR4A_RiverBank2 not found!!!");
			}
			if (r5 == null) {
				System.out.println("RR4B_RiverBank2 not found!!!");
			}

			managerService.planFlight("SBFD-2", "RR2_SouthArea", r1.getWaypoints());
			managerService.planFlight("SBFD-3", "RR3A_RiverBank", r2.getWaypoints());
			managerService.planFlight("SBFD-3", "RR3B_RiverBank", r3.getWaypoints());
			managerService.planFlight("SBFD-4", "RR4A_RiverBank2", r4.getWaypoints());
			managerService.planFlight("SBFD-4", "RR4B_RiverBank2", r5.getWaypoints());
			Thread.sleep(2000);

		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static FlightRouteInfo getRouteByName(String name, List<FlightRouteInfo> allRoutes) {
		for (FlightRouteInfo r : allRoutes) {
			if (r.getName().equals(name)) {
				return r;
			}

		}
		return null;
	}

	static Random rand = new Random();

}
