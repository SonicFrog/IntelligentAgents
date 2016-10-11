package ch.epfl.ia;

import java.util.HashMap;

import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;

import logist.task.Task;

import logist.topology.Topology.City;

public class ActionTable {
    private final HashMap<AgentState, SimpleAction> best;

    public ActionTable(HashMap<AgentState, SimpleAction> best) {
        this.best = best;
    }

    public Action best(City current, Task avail) {
        if (avail == null) {
            // Need to move to best city!
            AgentState s = new AgentState(current, false, null);
            SimpleMove move = (SimpleMove) best.get(s);
            return new Move(move.to);
        }

        AgentState state = new AgentState(current, true, avail.deliveryCity);
        SimpleAction bestAction = best.get(state);

        if (bestAction.isMove()) {
            return new Move(((SimpleMove) bestAction).to);
        }

        return new Pickup(avail);
    }
}
