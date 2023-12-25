package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import edu.nd.dronology.services.core.info.UAVTypeRegistrationInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IUAVTypeRegistrationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.registration.types.UAVTypeRegistrationService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVTypeRegistrationServiceRemoteFacade extends AbstractRemoteFacade implements IUAVTypeRegistrationRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVTypeRegistrationServiceRemoteFacade.class);
	private static volatile UAVTypeRegistrationServiceRemoteFacade INSTANCE;

	protected UAVTypeRegistrationServiceRemoteFacade() throws RemoteException {
		super(UAVTypeRegistrationService.getInstance());
	}

	public static IUAVTypeRegistrationRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (UAVTypeRegistrationServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new UAVTypeRegistrationServiceRemoteFacade();
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
		return UAVTypeRegistrationService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		UAVTypeRegistrationService.getInstance().transmitToServer(id, content);

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
	public Collection<UAVTypeRegistrationInfo> getItems() throws RemoteException {
		return UAVTypeRegistrationService.getInstance().getItems();
	}

	@Override
	public UAVTypeRegistrationInfo createItem() throws RemoteException, DronologyServiceException {
		return UAVTypeRegistrationService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}



}