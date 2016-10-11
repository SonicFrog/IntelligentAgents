package ch.epfl.ia;

import logist.topology.Topology;
import logist.topology.Topology.City;

import logist.task.TaskDistribution;


/**
 * Given a start state, an action and a target state computes the probability
 * that doing this action will result in transition to the target state
 *
 * @author Ogier Bouvier
 **/
public class ProbabilityTable {

    public final TaskDistribution td;

    public ProbabilityTable(TaskDistribution td) {
        this.td = td;
    }

    public double transitionProbability(AgentState start, SimpleAction a,
                                        AgentState target) {
        double probability = 0.0;
        if (start.isPossibleAction(a)) {
            City to = a.isDelivery() ?
                start.destCity :
                ((SimpleMove) a).to;

            if (to.equals(target.destCity)) {
                if (target.hasTask) {
                    // We need to compute the probability to have a task in destination
                    probability = td.probability(target.currentCity, target.destCity);
                } else {
                    // Probability to have no task at destination
                    probability = td.probability(target.currentCity, null);
                }
            }
        }

        return probability;
    }
}
