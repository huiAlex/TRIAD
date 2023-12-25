package edu.nd.dronology.core.status;

public interface IDronologyChangeListener {

	void notifyUAVRemoved(String uavid);

	void notifyGCSShutdown(String groundstationid);

}
