package edu.nd.dronology.gstation.connector;

public interface IUAVSafetyValidator {

	boolean validate(String uavid, String safetyCase);

}
