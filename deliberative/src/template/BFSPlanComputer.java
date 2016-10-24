package ch.epfl.ia.deliberative;

import logist.plan.Plan;
import logist.task.TaskSet;

/**
 * Computes plan according to the BFS algorithm
 **/
public class BFSPlanComputer implements PlanComputer {
    public StateNode computePath(Vehicle v, TaskSet ts) {
        StateNode root = new StateNode(start);

        // TODO: compute plan with BFS strategy

        return root;
    }
}
