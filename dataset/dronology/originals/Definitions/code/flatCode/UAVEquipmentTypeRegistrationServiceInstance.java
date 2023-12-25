package edu.nd.dronology.services.instances.registration.equipment;

import java.io.File;

import java.util.Set;

import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.api.ServiceInfo;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.UAVEquipmentTypeRegistrationInfo;
import edu.nd.dronology.services.core.items.IUAVEquipmentTypeRegistration;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.persistence.UAVEquipmentTypeRegistrationPersistenceProvider;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.instances.DronologyElementFactory;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class UAVEquipmentTypeRegistrationServiceInstance extends AbstractFileTransmitServiceInstance<UAVEquipmentTypeRegistrationInfo>
		implements IFileChangeNotifyable, IUAVEquipmentTypeRegistrationServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(UAVEquipmentTypeRegistrationServiceInstance.class);

	private static final int ORDER = 2;

	public static final String EXTENSION = DronologyConstants.EXTENSION_EQUIPMENT_TYPEREGISTRATION;


	public UAVEquipmentTypeRegistrationServiceInstance() {
		super(ServiceIds.SERVICE_EQUIP_TYPE_REGISTRATION, "UAV Equipment TypeRegistration Management", EXTENSION);
	}

	@Override
	protected Class<?> getServiceClass() {
		return UAVEquipmentTypeRegistrationServiceInstance.class;
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
	public UAVEquipmentTypeRegistrationInfo createItem() throws DronologyServiceException {
		UAVEquipmentTypeRegistrationPersistenceProvider persistor = UAVEquipmentTypeRegistrationPersistenceProvider.getInstance();
		IUAVEquipmentTypeRegistration specification = DronologyElementFactory.createNewUAVEquipmentTypeSpecification();
		specification.setName("New-UAVEquipmentTypeSpecification"); 
		String savePath = FileUtil.concat(storagePath, specification.getId(), EXTENSION);

		try {
			persistor.saveItem(specification, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating drone euqipment: " + e.getMessage());
		}
		return new UAVEquipmentTypeRegistrationInfo(specification.getName(), specification.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getDroneSpecificationLocation();
		return path;
	}

	@Override
	protected UAVEquipmentTypeRegistrationInfo fromFile(String id, File file) throws Throwable {
		IUAVEquipmentTypeRegistration atm = UAVEquipmentTypeRegistrationPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		UAVEquipmentTypeRegistrationInfo info = new UAVEquipmentTypeRegistrationInfo(atm.getName(), id);
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
