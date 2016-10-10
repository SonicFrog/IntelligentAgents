package ch.epfl.ia;

import java.util.List;
import java.util.Random;
import java.util.HashMap;
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

public class WorldState {
    private final Random random = new Random();
    private final Topology topo;
    private final TaskDistribution dist;

    private HashMap<AgentState, Action> bestActions = new HashMap<>();

    public WorldState(Topology topo, TaskDistribution dist) {
        this.topo = topo;
        this.dist = dist;
    }

    private List<Action> listActions(AgentState state) {
        List<Action> actions = new ArrayList<>();

        assert state != null;

        for (City c : topo.cities()) {
            if (c == state.currentCity && state.currentTask != null) {
                actions.clear();

                if (state.currentTask.deliveryCity == c) {
                    actions.add(new Delivery(state.currentTask));
                } else {
                    actions.add(new Move(state.currentTask.deliveryCity));
                }

                return actions;
            } else if (c == state.currentCity && state.currentTask == null) {
                if (state.availableTask != null) {
                    actions.add(new Pickup(state.availableTask));
                }
            } else {
                actions.add(new Move(c));
            }
        }

        return actions;
    }

    private Action best(AgentState state) {
        return bestActions.getOrDefault(
            state,
            new Move(state.currentCity.randomNeighbor(random)));
    }

    private City bestDestination(City from, Task currentTask) {
        if (currentTask != null) {
            return currentTask.deliveryCity;
        }

        City best = from;

        for (City c : topo.cities()) {
            if (c.equals(from)) {

            } else {

            }
        }

        return null;
    }

    public AgentState apply(AgentState state, Move action, Task avail) {
        return new AgentState(
            action.accept(new CityExtractor()),
            state.currentTask,
            avail
        );
    }

    public AgentState apply(AgentState state, Delivery action, Task avail) {
        return new AgentState(
            state.currentCity,
            null,
            avail
        );
    }

    public AgentState apply(AgentState state, Pickup action, Task avail) {
        return new AgentState(
            state.currentCity,
            action.accept(new TaskExtractor()),
            avail
        );
    }

    private double averageReward(City from) {
        double acc = 0.0;

        for (int i = 0; i < topo.cities().size(); i++) {
            City c = topo.cities().get(i);

            if (c == from) {
                continue;
            }

            acc += rewardFor(from, c);
        }

        return acc / (topo.cities().size() - 1);
    }

    private double rewardFor(City from, City to) {
        return ((double) dist.reward(from, to)) * dist.probability(from, to);
    }

    public boolean isGoodEnough() {
        // TODO: stub
        return false;
    }

    private static class AgentState {
        public final City currentCity;
        public final Task currentTask;
        public final Task availableTask;

        public AgentState(City city, Task task, Task avail) {
            currentCity = city;
            currentTask = task;
            availableTask = avail;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof AgentState)) {
                return false;
            }

            AgentState that = (AgentState) other;

            return this.currentCity.equals(that.currentCity) &&
                this.currentTask.equals(that.currentTask);
        }
    }

    public class TaskExtractor implements ActionHandler<Task> {
        @Override
        public Task moveTo(Topology.City city) {
            return null;
        }

        @Override
        public Task deliver(Task t) {
            return t;
        }

        @Override
        public Task pickup(Task t) {
            return t;
        }
    }

    public class CityExtractor implements ActionHandler<City> {
        @Override
        public City moveTo(Topology.City city) {
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
