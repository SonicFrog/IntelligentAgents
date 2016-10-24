package ch.epfl.ia.deliberative;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.TaskSet;

public interface PlanComputer {
    public StateNode computePlan(Vehicle v, TaskSet ts);
}
