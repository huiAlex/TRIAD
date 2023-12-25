package edu.nd.dronology.services.launch;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.services.core.info.DroneInitializationInfo;
import edu.nd.dronology.services.core.info.DroneInitializationInfo.DroneMode;
import edu.nd.dronology.services.core.info.FlightRouteInfo;
import edu.nd.dronology.services.core.info.MissionInfo;
import edu.nd.dronology.services.core.info.UAVMappingInfo;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IMissionPlanningRemoteService;
import edu.nd.dronology.services.core.remote.IRemoteManager;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class MissionExecutionTester {

	private static final String ADDRESS_SCHEME = "rmi://%s:%s/Remote";

	public static void main(String[] args) {
		try {
			// Flying Field
			LlaCoordinate cord1 = new LlaCoordinate(41.694116, -86.253591, 0);
			LlaCoordinate cord2 = new LlaCoordinate(41.519400, -86.239527, 0);
			LlaCoordinate cord4 = new LlaCoordinate(41.717158, -86.228932, 0);

			IRemoteManager manager = (IRemoteManager) Naming.lookup(String.format(ADDRESS_SCHEME, "localhost", 9779));

			IMissionPlanningRemoteService service = (IMissionPlanningRemoteService) manager
					.getService(IMissionPlanningRemoteService.class);

			IDroneSetupRemoteService dservice = (IDroneSetupRemoteService) manager
					.getService(IDroneSetupRemoteService.class);

			DroneInitializationInfo inff = new DroneInitializationInfo("frank", DroneMode.MODE_VIRTUAL, "IRIS+", cord1);
		//	dservice.initializeDrones(inff);

			Collection<MissionInfo> items = service.getItems();
			System.out.println("INFIS:" + items.size());
			for (MissionInfo mi : items) {
				System.out.println("MI:" + mi.getName());
				if (mi.getName().equals("mission1")) {
					UAVMappingInfo mapping = new UAVMappingInfo();
					mapping.addAttribute("uav1", "frank");
					service.executeMissionPlan(mi,mapping);
				}
			}
			//

			//
			// DroneInitializationInfo inff2 = new DroneInitializationInfo("S&R-UAV2",
			// DroneMode.MODE_VIRTUAL, "IRIS+",
			// cord4);
			// dservice.initializeDrones(inff2);
			//
			// DroneInitializationInfo inff3 = new DroneInitializationInfo("S&R-UAV3",
			// DroneMode.MODE_VIRTUAL, "IRIS+",
			// cord4);
			// dservice.initializeDrones(inff3);

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

	static Random rand = new Random();

	private static FlightRouteInfo getRandomRoute(List<FlightRouteInfo> allRoutes) {
		int routeSize = allRoutes.size();

		int randomNumber = rand.nextInt(routeSize);

		return allRoutes.get(randomNumber);

	}

}
