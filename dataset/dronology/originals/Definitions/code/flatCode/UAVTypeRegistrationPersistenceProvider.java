package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IUAVRegistration;
import edu.nd.dronology.services.core.items.IUAVTypeRegistration;
import edu.nd.dronology.services.core.persistence.internal.UAVTypeRegistrationXStreamPersistor;


/**
 * Provider implementation for {@link IUAVRegistration}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class UAVTypeRegistrationPersistenceProvider extends AbstractItemPersistenceProvider<IUAVTypeRegistration> {

	public UAVTypeRegistrationPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new UAVTypeRegistrationXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static UAVTypeRegistrationPersistenceProvider getInstance() {
		return new UAVTypeRegistrationPersistenceProvider();
	}

}
