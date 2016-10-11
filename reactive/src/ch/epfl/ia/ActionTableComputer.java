package ch.epfl.ia;

import java.util.Set;
import java.util.HashMap;

import logist.simulation.Vehicle;

import logist.topology.Topology;

/**
 * Class used to compute the best actions table for some vehicle
 **/
public class ActionTableComputer {

    private final RewardTable reward;
    private final ProbabilityTable probs;
    private final Topology topo;
    private final double discount;

    public ActionTableComputer(RewardTable reward, ProbabilityTable probs,
                               Topology topo, double discount) {
        this.reward = reward;
        this.probs = probs;
        this.topo = topo;
        this.discount = discount;
    }

    /**
     * Uses the RLA algorithm to compute the best action for each state
     **/
    public ActionTable computeBestActions(Vehicle car) {
        Set<AgentState> states = AgentState.generateStates(topo.cities());
        Set<SimpleAction> actions = SimpleAction.generateAllActions(topo.cities());

        HashMap<AgentState, Double> stateValues = new HashMap<>();
        HashMap<AgentState, SimpleAction> bestActions = new HashMap<>();

        boolean goodEnough = false;
        boolean changed = false;

        for (AgentState s : states) {
            stateValues.put(s, 0.0);
        }

        while (!goodEnough) {
            for (AgentState s : states) {
                SimpleAction bestAction = null;
                double bestValue = Double.NEGATIVE_INFINITY;

                for (SimpleAction a : s.findLegalActions(actions)) {
                    double finalQ = 0.0;
                    double R = reward.reward(s, a, car);
                    double sigma = 0.0;

                    for (AgentState nextState : states) {
                        double p = probs.transitionProbability(s, a, nextState);
                        sigma += stateValues.get(nextState) * p;
                    }

                    finalQ = discount * sigma + R;

                    if (finalQ > bestValue) {
                        bestValue = finalQ;
                        bestAction = a;
                    }
                }

                if (stateValues.put(s, bestValue) != bestValue) {
                    changed = true;
                }

                bestActions.put(s, bestAction);
            }

            goodEnough = !changed;
            changed = false;
        }

        return new ActionTable(bestActions);
    }
}
