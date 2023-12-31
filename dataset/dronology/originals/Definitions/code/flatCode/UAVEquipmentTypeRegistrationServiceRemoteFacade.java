package edu.nd.dronology.services.facades;

import java.rmi.RemoteException;

import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import edu.nd.dronology.services.core.info.UAVEquipmentTypeRegistrationInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IUAVEquipmentTypeRegistrationRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.instances.registration.equipment.UAVEquipmentTypeRegistrationService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVEquipmentTypeRegistrationServiceRemoteFacade extends AbstractRemoteFacade implements IUAVEquipmentTypeRegistrationRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVEquipmentTypeRegistrationServiceRemoteFacade.class);
	private static volatile UAVEquipmentTypeRegistrationServiceRemoteFacade INSTANCE;

	protected UAVEquipmentTypeRegistrationServiceRemoteFacade() throws RemoteException {
		super(UAVEquipmentTypeRegistrationService.getInstance());
	}

	public static IUAVEquipmentTypeRegistrationRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (UAVEquipmentTypeRegistrationServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new UAVEquipmentTypeRegistrationServiceRemoteFacade();
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
		return UAVEquipmentTypeRegistrationService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		UAVEquipmentTypeRegistrationService.getInstance().transmitToServer(id, content);

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
	public Collection<UAVEquipmentTypeRegistrationInfo> getItems() throws RemoteException {
		return UAVEquipmentTypeRegistrationService.getInstance().getItems();
	}

	@Override
	public UAVEquipmentTypeRegistrationInfo createItem() throws RemoteException, DronologyServiceException {
		return UAVEquipmentTypeRegistrationService.getInstance().createItem();
	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}



}