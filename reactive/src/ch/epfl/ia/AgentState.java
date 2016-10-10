package ch.epfl.ia;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;

import logist.plan.Action;
import logist.plan.ActionHandler;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.plan.Action.Delivery;

import logist.topology.Topology;
import logist.topology.Topology.City;

import logist.task.Task;
import logist.task.TaskDistribution;

public class AgentState {

    public final City currentCity;
    public final City destCity;
    public final boolean hasTask;

    private List<SimpleActions> possibleActions;
    private List<AgentState> possibleStates;

    public AgentState(City current, City dest, boolean task) {

        if (current == null) {
            throw new IllegalArgumentException("The agent must be in a city");
        }

        if (dest == null && task) {
            throw new IllegalArgumentException("The destination for a task can't be null");
        }

        currentCity = current;
        destCity = to;
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

    public boolean isPossibleAction(Action a) {
        boolean isDelivery = a.accept(new IsDeliveryVisitor());

        if (isDelivery && !hasTask)
            return false;

        if (isDelivery && hasTask)
            return true;

        if (a.accept(new IsMoveVisitor())) {
            Move action = (Move) a;
            return action.accept(new CityExtractor()).equals()
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        State that = (State) other;
        boolean task = this.hasTask == that.hasTask;
        boolean sameDest = that.destCity != null && this.destCity != null &&
            (that.destCity.equals(this.destCity));

        if (that.destCity == null && this.destCity == null)
            return task;

        return this.currentCity.equals(that.currentCity) &&
            sameDest && task;
    }

    public static List<AgentState> generateStates(List<City> cities) {
        if (possibleStates != null) {
            return possibleStates;
        }

        possibleStates = new ArrayList<>();

        for (City current : cities) {
            // Being in every city with no task available
            possibleStates.add(new WorldState(current, null, false));
            for (City dest : cities) {
                if (dest.equals(current))
                    continue;

                possibleStates.add(new WorldState(current, true, dest));
            }
        }

        return possibleStates;
    }

    public class IsDeliveryVisitor implements ActionHandler<Boolean> {
        @Override
        public Boolean moveTo(City city) {
            return false;
        }

        @Override
        public Boolean deliver(Task t) {
            return true;
        }

        @Override
        public Boolean pickup(Task t) {
            return false;
        }
    }

    public class CityExtractor implements ActionHandler<City> {
        @Override
        public City moveTo(City city) {
            return city;
        }

        @Override
        public City deliver(Task t) {
            return t.deliveryCity;
        }

        @Override
        public City pickup(Task t) {
            return t.pickupCity;
        }
    }
}
