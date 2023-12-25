package edu.nd.dronology.services.instances.registration.uavs;

import java.io.File;
import java.util.Set;

import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.UAVRegistrationInfo;
import edu.nd.dronology.services.core.items.IUAVRegistration;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.persistence.UAVRegistrationPersistenceProvider;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVRegistrationServiceInstance extends AbstractFileTransmitServiceInstance<UAVRegistrationInfo>
		implements IFileChangeNotifyable, IUAVRegistrationServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVRegistrationServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DronologyConstants.EXTENSION_REGISTRATION;

	public UAVRegistrationServiceInstance() {
		super(ServiceIds.SERVICE_REGISTRATION, "UAV Rgistration Management", EXTENSION);
	}

	@Override
	protected Class<?> getServiceClass() {
		return UAVRegistrationServiceInstance.class;
	}

	@Override
	protected int getOrder() {
		return ORDER;
	}

	@Override
	protected String getPropertyPath() {
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
		reloadItems();
	}

	@Override
	protected void doStopService() throws Exception {
		fileManager.tearDown();
	}

	@Override
	public ServiceInfo getServiceInfo() {
		ServiceInfo sInfo = super.getServiceInfo();
		sInfo.addAttribute(ServiceInfo.ATTRIBUTE_TYPE, ServiceInfo.ATTRIBUTE_FILE);
		return sInfo;
	}

	@Override
	public UAVRegistrationInfo createItem() throws DronologyServiceException {
		UAVRegistrationPersistenceProvider persistor = UAVRegistrationPersistenceProvider.getInstance();
		IUAVRegistration specification = DronologyElementFactory.createNewUAVRegistration();
		specification.setName("New-DroneSpecification");
		String savePath = FileUtil.concat(storagePath, specification.getId(), EXTENSION);

		try {
			persistor.saveItem(specification, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating drone euqipment: " + e.getMessage());
		}
		return new UAVRegistrationInfo(specification.getName(), specification.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getDroneSpecificationLocation();
		return path;
	}

	@Override
	protected UAVRegistrationInfo fromFile(String id, File file) throws Throwable {
		IUAVRegistration atm = UAVRegistrationPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		UAVRegistrationInfo info = new UAVRegistrationInfo(atm.getName(), id);
		info.setType(atm.getType());
		return info;
	}

	@Override
	protected boolean hasProperties() {
		return false;
	}

	@Override
	public void notifyFileChange(Set<String> changed) {
		for (String s : changed) {
			updateItem(s);
		}
		super.notifyFileChange(changed);
		for (String s : changed) {
			String id = s.replace("." + extension, "");
			if (!itemmap.containsKey(id)) {

			}
		}
	}

	private void updateItem(String s) {
		System.out.println("UPDATE");

	}

}
