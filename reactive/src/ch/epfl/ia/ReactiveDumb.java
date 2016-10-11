package ch.epfl.ia;

import java.util.Random;

import logist.agent.Agent;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.behavior.ReactiveBehavior;
import logist.simulation.Vehicle;
import logist.topology.Topology;
import logist.topology.Topology.City;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;

/**
 * A dumb agent for comparison with the intelligent one.
 * This agent always picks up task when they are available,
 * otherwise it just moves to a random neighbor.
 **/
public class ReactiveDumb implements ReactiveBehavior {

    private Agent agent;
    private Random r = new Random();
    private int step = 0;

    @Override
    public void setup(Topology t, TaskDistribution td, Agent agent) {
        this.agent = agent;
    }

    @Override
    public Action act(Vehicle v, Task available) {
        // No need for output we can just run it in parallel with the other agents

        Action a;

        if (available == null) {
            return new Move(v.getCurrentCity().randomNeighbor(r));
        }
        return new Pickup(available);
    }
}
