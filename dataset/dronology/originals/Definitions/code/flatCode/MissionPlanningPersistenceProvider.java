package edu.nd.dronology.services.core.persistence;

import edu.nd.dronology.services.core.items.IMissionPlan;
import edu.nd.dronology.services.core.persistence.internal.MissionPlanningXStreamPersistor;


/**
 * Provider implementation for {@link IMissionPlan}.<br>
 * Details see {@link AbstractItemPersistenceProvider}
 * 
 * @author Michael Vierhauser
 * 
 */
public class MissionPlanningPersistenceProvider extends AbstractItemPersistenceProvider<IMissionPlan> {

	public MissionPlanningPersistenceProvider() {
		super();
	}

	@Override
	protected void initPersistor() {
		PERSISTOR = new MissionPlanningXStreamPersistor();

	}

	@Override
	protected void initPersistor(String type) {
		initPersistor();
	}

	public static MissionPlanningPersistenceProvider getInstance() {
		return new MissionPlanningPersistenceProvider();
	}

}
