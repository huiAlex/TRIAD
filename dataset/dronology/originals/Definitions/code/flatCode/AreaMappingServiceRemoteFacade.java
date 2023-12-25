package edu.nd.dronology.services.extensions.areamapping.facade;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.info.AreaMappingCategoryInfo;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.listener.IItemChangeListener;
import edu.nd.dronology.services.core.remote.IAreaMappingRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.extensions.areamapping.instances.AreaMappingService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.remote.AbstractRemoteFacade;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider; 

/**
 * 
 * 
 * 
 * @author Michael Vierhauser
 *
 */
public class AreaMappingServiceRemoteFacade extends AbstractRemoteFacade implements IAreaMappingRemoteService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4580658378477037955L;
	private static final ILogger LOGGER = LoggerProvider.getLogger(AreaMappingServiceRemoteFacade.class);
	private static volatile AreaMappingServiceRemoteFacade INSTANCE;

	protected AreaMappingServiceRemoteFacade() throws RemoteException {
		super(FlightManagerService.getInstance());
	}

	public static IAreaMappingRemoteService getInstance() throws RemoteException {
		if (INSTANCE == null) {
			try {
				synchronized (AreaMappingServiceRemoteFacade.class) {
					if (INSTANCE == null) {
						INSTANCE = new AreaMappingServiceRemoteFacade();
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
		return AreaMappingService.getInstance().requestFromServer(id);
	}

	@Override
	public void transmitToServer(String id, byte[] content) throws RemoteException, DronologyServiceException {
		AreaMappingService.getInstance().transmitToServer(id, content);
	}

	@Override
	public boolean addItemChangeListener(IItemChangeListener listener) throws RemoteException {
		return AreaMappingService.getInstance().addItemChangeListener(listener);
	}

	@Override
	public boolean removeItemChangeListener(IItemChangeListener listener) throws RemoteException {
		return AreaMappingService.getInstance().removeItemChangeListener(listener);

	}

	@Override
	public Collection<AreaMappingInfo> getItems() throws RemoteException {
		return AreaMappingService.getInstance().getItems();

	}

	@Override
	public AreaMappingInfo createItem() throws RemoteException, DronologyServiceException {
		return AreaMappingService.getInstance().createItem();

	}

	@Override
	public void deleteItem(String itemid) throws RemoteException, DronologyServiceException {
		AreaMappingService.getInstance().deleteItem(itemid);

	}

	@Override
	public Collection<AreaMappingCategoryInfo> getMappingPathCategories() throws RemoteException {
		return AreaMappingService.getInstance().getMappingPathCategories();
	}

	@Override
	public GeneratedMappedArea generateAreaMapping(AreaMappingInfo info)
			throws DronologyServiceException, RemoteException {
		return AreaMappingService.getInstance().generateAreaMapping(info);
	}
	
	@Override
	public GeneratedMappedArea generateAreaMapping(AreaMappingInfo info, Collection<IUAVProxy> selectedUAVs)
			throws DronologyServiceException, RemoteException {
		return AreaMappingService.getInstance().generateAreaMapping(info,selectedUAVs);
	}

	@Override
	public void executeAreaMapping(GeneratedMappedArea area) throws DronologyServiceException, RemoteException {
		AreaMappingService.getInstance().executeAreaMapping(area);

	}



}