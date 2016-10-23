package template;

/* import table */
import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Action;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;
import java.util.HashSet;
import java.util.Set;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeTemplate implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }

	/* Environment */
	Topology topology;
	TaskDistribution td;

	/* the properties of the agent */
	Agent agent;
	int capacity;

	/* the planning class */
	Algorithm algorithm;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		this.topology = topology;
		this.td = td;
		this.agent = agent;

		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");

		// Throws IllegalArgumentException if algorithm is unknown
		algorithm = Algorithm.valueOf(algorithmName.toUpperCase());

		// ...
	}

	@Override
	public Plan plan(Vehicle vehicle, TaskSet tasks) {
		Plan plan;

		// Compute the plan with the selected algorithm.
		switch (algorithm) {
		case ASTAR:
			// ...
			plan = naivePlan(vehicle, tasks);
			break;
		case BFS:
			// ...
			plan = bfs(vehicle, tasks);
			break;
		default:
			throw new AssertionError("Should not happen.");
		}
		return plan;
	}

	private class PlanTask {
		private City current;
		private Plan plan;
		private TaskSet notPicked;
		private TaskSet carrying;

		public PlanTask(City current, Plan plan, TaskSet notPicked, TaskSet carrying) {
			assert TaskSet.intersect(notPicked, carrying).size() == 0;

			this.current = current;
			this.plan = plan;
			this.notPicked = notPicked;
			this.carrying = carrying;
		}

		public PlanTask moveAndPickupTask(Task task) {
			assert notPicked.contains(task);

			Plan plan = this.getPlan();

			for (City city : this.current.pathTo(task.pickupCity))
				plan.appendMove(city);
			plan.appendPickup(task);

			TaskSet newNotPicked = this.getNotPicked();
			TaskSet newCarrying = this.getCarrying();

			newNotPicked.remove(task);
			newCarrying.add(task);

			return new PlanTask(this.current, plan, newNotPicked, newCarrying);
		}

		public PlanTask moveAndDeliverTask(Task task) {
			assert carrying.contains(task);

			Plan plan = this.getPlan();

			for (City city : this.current.pathTo(task.deliveryCity))
				plan.appendMove(city);
			plan.appendDelivery(task);

			TaskSet newCarrying = this.getCarrying();
			newCarrying.remove(task);

			return new PlanTask(this.current, plan, this.notPicked, newCarrying);
		}

		private Plan dupPlan(Plan plan) {
			Plan newPlan = new Plan(this.current);

			for(Action action : plan)
				newPlan.append(action);

			return newPlan;
		}

		public Plan getPlan() {
			return dupPlan(this.plan);
		}

		public TaskSet getNotPicked() {
			return TaskSet.copyOf(this.notPicked);
		}

		public TaskSet getCarrying() {
			return TaskSet.copyOf(this.carrying);
		}
	}

	private boolean canCarry(Vehicle vehicle, TaskSet carrying, Task toAdd) {
		int totalWeight = 0;
		for (Task task : carrying)
			totalWeight += task.weight;

		return vehicle.capacity() - totalWeight > toAdd.weight;
	}

	private Set<PlanTask> getNextPlanTasks(Vehicle vehicle, PlanTask planTask) {
		Set<PlanTask> ret = new HashSet<>();

		for (Task task: planTask.getNotPicked()) {
			if (!canCarry(vehicle, planTask.getCarrying(), task))
				continue;

			PlanTask pt = planTask.moveAndPickupTask(task);

			ret.add(pt);
		}

		for (Task task: planTask.getCarrying()) {
			PlanTask pt = planTask.moveAndDeliverTask(task);

			ret.add(pt);
		}

		return ret;
	}

	private Plan bfs(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		PlanTask planTask = new PlanTask(current, plan, tasks, TaskSet.noneOf(tasks));
		Set<PlanTask> planTasks = new HashSet();
		planTasks.add(planTask);

		boolean changed = true;
		while (changed) {
			Set<PlanTask> nextPlanTasks = new HashSet();
			for (PlanTask PlanTask : planTasks) {
				Set<PlanTask> nexts = getNextPlanTasks(vehicle, planTask);
				nextPlanTasks.addAll(nexts);
			}

			changed = !planTask.equals(nextPlanTasks);
			planTasks = nextPlanTasks;
		}

		double smallest = Integer.MAX_VALUE;
		PlanTask best = null;
		for (PlanTask pt : planTasks) {
			double distance = pt.getPlan().totalDistance();

			if (distance < smallest) {
				smallest = distance;
				best = pt;
			}
		}

		return best.getPlan();
	}

	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}

	@Override
	public void planCancelled(TaskSet carriedTasks) {

		if (!carriedTasks.isEmpty()) {
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.
		}
	}
}
