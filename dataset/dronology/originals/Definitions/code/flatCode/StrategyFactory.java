package edu.nd.dronology.services.extensions.areamapping.selection;

import edu.nd.dronology.services.extensions.areamapping.selection.random.RandomRouteSelector;

public class StrategyFactory {

	public static IRouteSelectionStrategy getSelectionStrategy() {
		return new RandomRouteSelector();
	}

}
