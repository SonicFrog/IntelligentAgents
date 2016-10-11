package ch.epfl.ia;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

import logist.plan.Action;
import logist.plan.ActionHandler;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.plan.Action.Delivery;

import logist.topology.Topology;
import logist.topology.Topology.City;

import logist.task.Task;
import logist.task.TaskDistribution;

/**
 * The state representation and utility class
 * @author Ogier Bouvier
 **/
public class AgentState {

    public final City currentCity;
    public final City destCity;
    public final boolean hasTask;

    private static Set<AgentState> possibleStates;

    private Set<SimpleAction> legalActions;

    public AgentState(City current, boolean task, City dest) {

        if (current == null) {
            throw new IllegalArgumentException("The agent must be in a city");
        }

        if (dest == null && task) {
            throw new IllegalArgumentException("The destination for a task can't be null");
        }

        currentCity = current;
        destCity = dest;
        hasTask = task;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currentCity == null) ? 0 : currentCity.hashCode());
        result = prime * result + (hasTask ? 1231 : 1237);
        result = prime * result + ((destCity == null) ? 0 : destCity.hashCode());
        return result;
    }

    public boolean isPossibleAction(SimpleAction a) {
        if (a.isDelivery() && hasTask)
            return true;

        if (a.isDelivery() && !hasTask)
            return false;

        SimpleMove action = (SimpleMove) a;

        if (action.from.equals(currentCity) && !action.to.equals(currentCity)) {
            if (currentCity.hasNeighbor(action.to)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        AgentState that = (AgentState) other;
        boolean task = this.hasTask == that.hasTask;
        boolean sameDest = that.destCity != null && this.destCity != null &&
            (that.destCity.equals(this.destCity));

        if (that.destCity == null && this.destCity == null)
            return task;

        return this.currentCity.equals(that.currentCity) &&
            sameDest && task;
    }

    public Set<SimpleAction> findLegalActions(Set<SimpleAction> all) {
        if (legalActions == null) {
            legalActions = all.stream().filter(
                a -> isPossibleAction(a))
                .collect(Collectors.toCollection(HashSet::new));
        }

        return legalActions;
    }

    public static Set<AgentState> generateStates(List<City> cities) {
        if (possibleStates != null) {
            return possibleStates;
        }

        possibleStates = new HashSet<>();

        for (City current : cities) {
            // Being in every city with no task available
            possibleStates.add(new AgentState(current, false, null));
            for (City dest : cities) {
                if (dest.equals(current))
                    continue;

                possibleStates.add(new AgentState(current, true, dest));
            }
        }

        return possibleStates;
    }
}
