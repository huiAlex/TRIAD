package edu.nd.dronology.core.collisionavoidance;

import edu.nd.dronology.core.collisionavoidance.guidancecommands.Command;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.goal.AbstractGoal;
import edu.nd.dronology.core.goal.IGoalSnapshot;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * 
 */
public class DroneSnapshotInternal implements DroneSnapshot {

    private String name;
    private final LlaCoordinate position;
    private final Vector3D velocity;
    private final Vector3D attitude;
    private String state;
    private Set<IGoalSnapshot> goals;


    private final ArrayList<Command> commands;

    public DroneSnapshotInternal(String name, LlaCoordinate position, Vector3D velocity, Vector3D attitude) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.attitude = attitude;
        this.commands = new ArrayList<>();
        this.goals = new HashSet<>();
    }

    @Override
    public LlaCoordinate getPosition() {
        return this.position;
    }

    @Override
    public ArrayList<Command> getCommands() {
        return commands;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Vector3D getVelocity() {
        return velocity;
    }

    @Override
    public Vector3D getAttitude() {
        return attitude;
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public Set<IGoalSnapshot> getGoals() {
        return goals;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setGoals(Set<IGoalSnapshot> goals) {
        this.goals = goals;
    }
}
