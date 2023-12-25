package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.persistence.internal.AreaMappingXStreamPersistor;
import edu.nd.dronology.services.core.persistence.internal.UAVRegistrationXStreamPersistor;


/**
 * Provider implementation for {@link IAreaMapping}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class AreaMappingPersistenceProvider extends AbstractItemPersistenceProvider<IAreaMapping> {

	public AreaMappingPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new AreaMappingXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static AreaMappingPersistenceProvider getInstance() {
		return new AreaMappingPersistenceProvider();
	}

}
