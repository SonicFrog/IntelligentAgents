package ch.epfl.ia;

import java.util.Set;
import java.util.HashMap;

import logist.simulation.Vehicle;

import logist.topology.Topology;

public class ActionTableComputer {

    private final RewardTable reward;
    private final ProbabilityTable probs;
    private final Topology topo;

    public ActionTableComputer(RewardTable reward, ProbabilityTable probs,
                               Topology topo, double discount) {
        this.reward = reward;
        this.probs = probs;
        this.topo = topo;
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

        while (!goodEnough) {
            for (AgentState s : states) {
                SimpleAction bestAction = null;
                double bestValue = Double.NEGATIVE_INFINITY;

                for (SimpleAction a : s.findLegalActions(actions)) {
                    double rewardVal = reward.reward(s, a, car);

                    if (rewardVal > bestValue) {
                        bestValue = rewardVal;
                        bestAction = a;
                    }
                }
            }
        }

        return new ActionTable(bestActions);
    }
}
