package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IUAVEquipmentTypeRegistration;
import edu.nd.dronology.services.core.items.IUAVRegistration;
import edu.nd.dronology.services.core.persistence.internal.UAVEquipmentTypeRegistrationXStreamPersistor;


/**
 * Provider implementation for {@link IUAVRegistration}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class UAVEquipmentTypeRegistrationPersistenceProvider extends AbstractItemPersistenceProvider<IUAVEquipmentTypeRegistration> {

	public UAVEquipmentTypeRegistrationPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new UAVEquipmentTypeRegistrationXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static UAVEquipmentTypeRegistrationPersistenceProvider getInstance() {
		return new UAVEquipmentTypeRegistrationPersistenceProvider();
	}

}
