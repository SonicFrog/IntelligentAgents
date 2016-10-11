package ch.epfl.ia;

import logist.simulation.Vehicle;

import logist.task.TaskDistribution;

import logist.topology.Topology.City;

/**
 * Reward table computing real reward using cost calculations
 * @author Ogier Bouvier
 **/
public class RewardTable {
    private final TaskDistribution dist;

    public RewardTable(TaskDistribution dist) {
        this.dist = dist;
    }

    public double reward(AgentState state, SimpleAction a, Vehicle v) {
        City from = state.currentCity;
        City to = a.isDelivery() ? state.destCity : ((SimpleMove) a).to;

        if (a.isMove()) {
            return 0; // Move does not reward anything T_T
        }

        double cost = from.distanceTo(to) * v.costPerKm();

        return dist.reward(from, to) - cost;
    }
}
