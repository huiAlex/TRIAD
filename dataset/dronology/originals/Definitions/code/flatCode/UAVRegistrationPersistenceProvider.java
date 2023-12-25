package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IUAVRegistration;
import edu.nd.dronology.services.core.persistence.internal.UAVRegistrationXStreamPersistor;


/**
 * Provider implementation for {@link IUAVRegistration}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class UAVRegistrationPersistenceProvider extends AbstractItemPersistenceProvider<IUAVRegistration> {

	public UAVRegistrationPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new UAVRegistrationXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static UAVRegistrationPersistenceProvider getInstance() {
		return new UAVRegistrationPersistenceProvider();
	}

}
