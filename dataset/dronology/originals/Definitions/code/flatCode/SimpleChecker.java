package edu.nd.dronology.monitoring.simplechecker.checker;

import java.rmi.RemoteException;

import edu.nd.dronology.core.monitoring.ArtifactIdentifier;
import edu.nd.dronology.monitoring.service.IDroneMonitoringRemoteService;
import edu.nd.dronology.monitoring.simplechecker.BaseServiceProvider;
import edu.nd.dronology.services.core.remote.IFlightManagerRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;

public class SimpleChecker {
	private static volatile SimpleChecker INSTANCE = null;
	BaseServiceProvider provider = new BaseServiceProvider();

	private SimpleChecker() {

	}

	public static SimpleChecker getInstance() {
		if (INSTANCE == null) {
			synchronized (SimpleChecker.class) {
				if (INSTANCE == null) {
					INSTANCE = new SimpleChecker();
				}
			}
		}
		return INSTANCE;
	}

	public void init() {

		try {
			System.setProperty("java.rmi.server.hostname", "localhost");

			IDroneMonitoringRemoteService sevice = (IDroneMonitoringRemoteService) provider.getRemoteManager()
					.getService(IDroneMonitoringRemoteService.class);

			// ArtifactIdentifier id = new ArtifactIdentifier(ArtifactIdentifier.ROOT,
			// "INTERNAL");
			ArtifactIdentifier id = new ArtifactIdentifier(ArtifactIdentifier.ROOT, "INTERNAL");
			ArtifactIdentifier id2 = new ArtifactIdentifier(ArtifactIdentifier.ROOT, "GCS-DEFAULT");

			SimpleCheckerMessageHandler handler = new SimpleCheckerMessageHandler();
			sevice.registerMonitoringMessageHandler(handler, id);
			sevice.registerMonitoringMessageHandler(handler, id2);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void emergencyStop(String uavid) {
		try {
			IFlightManagerRemoteService sevice = (IFlightManagerRemoteService) provider.getRemoteManager()
					.getService(IFlightManagerRemoteService.class);
			sevice.pauseFlight(uavid);

		} catch (RemoteException | DronologyServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
