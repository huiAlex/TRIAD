package edu.nd.dronology.core.vehicle.manageddroneinternal;

public interface CommandExecutor {
    void process();
    boolean isFinished();
}
