package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.goal.AbstractGoal;
import edu.nd.dronology.core.goal.IGoalSnapshot;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.Set;

public interface DroneSnapshot {
    LlaCoordinate getPosition();
    ArrayList<Command> getCommands();
    String getName();
    Vector3D getVelocity();
    Vector3D getAttitude();
    String getState();
    Set<IGoalSnapshot> getGoals();
}
