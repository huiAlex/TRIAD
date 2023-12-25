package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import edu.nd.dronology.services.core.info.UAVRegistrationInfo;
import edu.nd.dronology.services.core.info.TypeRegistrationInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IUAVRegistrationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.registration.uavs.UAVRegistrationService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVRegistrationServiceRemoteFacade extends AbstractRemoteFacade implements IUAVRegistrationRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVRegistrationServiceRemoteFacade.class);
	private static volatile UAVRegistrationServiceRemoteFacade INSTANCE;

	protected UAVRegistrationServiceRemoteFacade() throws RemoteException {
		super(UAVRegistrationService.getInstance());
	}

	public static IUAVRegistrationRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (UAVRegistrationServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new UAVRegistrationServiceRemoteFacade();
					}
				}
			} catch (RemoteException e) {
				LOGGER.error(e);
			}
		}
		return INSTANCE;
	}

	@Override
	public byte[] requestFromServer(String id) throws RemoteException, DronologyServiceException {
		return UAVRegistrationService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		UAVRegistrationService.getInstance().transmitToServer(id, content);

	}

	@Override
	public boolean addItemChangeListener(IItemChangeListener listener) throws RemoteException {
		throw new NotImplementedException();
	}

	@Override
	public boolean removeItemChangeListener(IItemChangeListener listener) throws RemoteException {
		throw new NotImplementedException();
	}

	@Override
	public Collection<UAVRegistrationInfo> getItems() throws RemoteException {
		return UAVRegistrationService.getInstance().getItems();
	}

	@Override
	public UAVRegistrationInfo createItem() throws RemoteException, DronologyServiceException {
		return UAVRegistrationService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}



}