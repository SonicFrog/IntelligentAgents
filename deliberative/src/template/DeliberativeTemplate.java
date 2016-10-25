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
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeTemplate implements DeliberativeBehavior {

        enum Algorithm { BFS, ASTAR, NAIVE }

	private static int QUEUE_SIZE = 20000;

	/* Environment */
	Topology topology;
	TaskDistribution td;

	/* the properties of the agent */
	Agent agent;
	int capacity;

	/* the planning class */
	Algorithm algorithm;
	TaskSet carried;

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

		if (this.carried == null)
			this.carried = TaskSet.noneOf(tasks);

		// Compute the plan with the selected algorithm.
		switch (algorithm) {
		case ASTAR:
			// ...
			plan = astar(vehicle, tasks, carried);
			break;
		case BFS:
			// ...
			plan = bfs(vehicle, tasks, carried);
			break;
                case NAIVE:
                        plan = naivePlan(vehicle, tasks);
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

		private Task taskInCity(City city, TaskSet tasks) {
			for (Task t : tasks) {
				if (t.pickupCity.equals(city) && t.deliveryCity.equals(city))
					return t;
			}

			return null;
		}

		private void move(Plan plan, TaskSet newNotPicked, TaskSet newCarrying, City city) {
			for (City c: this.current.pathTo(city)) {
				plan.appendMove(c);

				int oldNotPickedSize = newNotPicked.size();
				int oldCarryingSize = newCarrying.size();
				newNotPicked.stream()
					.filter(t -> t.pickupCity.equals(c))
					.forEach(t -> {
						plan.appendPickup(t);
						newNotPicked.remove(t);
						newCarrying.add(t);
					});
				assert oldNotPickedSize - newNotPicked.size() == newCarrying.size() - oldCarryingSize;

				newCarrying.stream()
					.filter(t -> t.deliveryCity.equals(c))
					.forEach(t -> {
						plan.appendDelivery(t);
						newCarrying.remove(t);
					});
			}
		}

		public PlanTask moveAndPickupTask(Task task) {
			assert notPicked.contains(task);

			Plan plan = this.getPlan();
			TaskSet newNotPicked = this.getNotPicked();
			TaskSet newCarrying = this.getCarrying();

			move(plan, newNotPicked, newCarrying, task.pickupCity);

			return new PlanTask(task.pickupCity, plan, newNotPicked, newCarrying);
		}

		public PlanTask moveAndDeliverTask(Task task) {
			assert carrying.contains(task);

			Plan plan = this.getPlan();
			TaskSet newCarrying = this.getCarrying();
			TaskSet newNotPicked = this.getNotPicked();

			move(plan, newNotPicked, newCarrying, task.deliveryCity);

			return new PlanTask(task.deliveryCity, plan, newNotPicked, newCarrying);
		}

		public boolean equals(Object o) {
			if (o == this)
				return true;

			if (!(o instanceof PlanTask))
				return false;

			PlanTask that = (PlanTask) o;

			// using toString because framework do not redefinie equals
			return this.current.toString().equals(that.current.toString()) &&
				this.plan.toString().equals(that.plan.toString()) &&
				this.notPicked.equals(that.notPicked) &&
				this.carrying.equals(that.carrying);
		}

		public int hashCode() {
			return this.notPicked.hashCode() +
				this.carrying.hashCode();
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

		public double estimateCost() {
			double distToPick = this.notPicked.stream()
				.map(t -> this.current.distanceTo(t.pickupCity))
				.max(Double::compare).orElse(0.0);
			double distToDeliver = this.carrying.stream()
				.map(t -> this.current.distanceTo(t.deliveryCity))
				.max(Double::compare).orElse(0.0);
			return distToPick + distToDeliver;
		}
	}

	private boolean canCarry(Vehicle vehicle, TaskSet carrying, Task toAdd) {
		int totalWeight = 0;
		for (Task task : carrying)
			totalWeight += task.weight;

		return vehicle.capacity() - totalWeight > toAdd.weight;
	}

	private Stream<PlanTask> getNextPlanTasks(Vehicle vehicle, PlanTask planTask) {
		TaskSet currentCarrying = planTask.getCarrying();

		Stream<PlanTask> notPicked = planTask.getNotPicked().stream().parallel()
			.filter(t -> canCarry(vehicle, currentCarrying, t))
			.map(t -> planTask.moveAndPickupTask(t));
		Stream<PlanTask> carrying = planTask.getCarrying().stream().parallel()
			.map(t -> planTask.moveAndDeliverTask(t));

		return Stream.concat(notPicked, carrying);
	}

	private PlanTask getFinished(Set<PlanTask> planTasks) {
		for (PlanTask pt : planTasks)
			if (pt.getCarrying().size() == 0 && pt.getNotPicked().size() == 0) {
				System.out.println(pt.getPlan());
				return pt;
			}

		return null;
	}

	private Plan bfs(Vehicle vehicle, TaskSet tasks, TaskSet carried) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		PlanTask planTask = new PlanTask(current, plan, tasks, carried);
		Set<PlanTask> planTasks = new HashSet<>();
		planTasks.add(planTask);

		while (true) {
			planTasks = planTasks.stream().parallel()
				.flatMap(pt -> getNextPlanTasks(vehicle, pt))
				.collect(Collectors.toSet());

			PlanTask ts = getFinished(planTasks);
			if (ts != null)
				return ts.getPlan();

			System.out.println(planTasks.size());
		}
	}

	private Plan astar(Vehicle vehicle, TaskSet tasks, TaskSet carried) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		PlanTask planTask = new PlanTask(current, plan, tasks, carried);
		PriorityQueue<PlanTask> planTasks = new PriorityQueue<>(QUEUE_SIZE,
				(a, b) -> (int) (b.estimateCost() - a.estimateCost()));
		planTasks.add(planTask);

		while (true) {
			PlanTask pt = planTasks.poll();
			Set<PlanTask> succs = getNextPlanTasks(vehicle, pt).collect(Collectors.toSet());
			planTasks.addAll(succs);

			PlanTask ts = getFinished(succs);
			if (ts != null)
				return ts.getPlan();

			System.out.println(planTasks.size());
		}
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
		this.carried = carriedTasks;
	}
}
