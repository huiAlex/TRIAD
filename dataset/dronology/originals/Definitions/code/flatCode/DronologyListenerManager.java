package edu.nd.dronology.core.status;

import java.util.ArrayList;
import java.util.List;

public class DronologyListenerManager implements IDronologyChangeListener {

	private static final DronologyListenerManager INSTANCE = new DronologyListenerManager();

	public static DronologyListenerManager getInstance() {
		return INSTANCE;
	}

	private List<IDronologyChangeListener> listeners = new ArrayList<>();

	public void addListener(IDronologyChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void notifyUAVRemoved(String uavid) {
		for (IDronologyChangeListener l : listeners) {
			l.notifyUAVRemoved(uavid);
		}
	}

	@Override
	public void notifyGCSShutdown(String groundstationid) {
		for (IDronologyChangeListener l : listeners) {
			l.notifyGCSShutdown(groundstationid);
		}
	}

	public boolean removeListener(IDronologyChangeListener listener) {
		return listeners.remove(listener);

	}

}
