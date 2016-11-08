package template;

//the list of imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import logist.LogistSettings;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class CentralizedTemplate implements CentralizedBehavior {

	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private long timeout_setup;
	private long timeout_plan;
	private long time_start;

	private class TaskAction {
		public Task task;
		public boolean need_to_pickup;

		public TaskAction(Task task, boolean need_to_pickup) {
			this.task = task;
			this.need_to_pickup = need_to_pickup;
		}

		public TaskAction invert() {
			return new TaskAction(this.task, !this.need_to_pickup);
		}
	}

	@Override
	public void setup(Topology topology, TaskDistribution distribution,
			Agent agent) {

		// this code is used to get the timeouts
		LogistSettings ls = null;
		try {
			ls = Parsers.parseSettings("config/settings_default.xml");
		}
		catch (Exception exc) {
			System.out.println("There was a problem loading the configuration file.");
		}

		// the setup method cannot last more than timeout_setup milliseconds
		timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
		// the plan method cannot execute more than timeout_plan milliseconds
		timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);

		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
	}

	private long getRemainingTime() {
		long t = timeout_plan - (System.currentTimeMillis() - this.time_start);

		assert t > 0;

		return t;
	}

	private Vehicle getBiggestVehicule(Set<Vehicle> vehicles) {
		return vehicles.stream().max((a, b) -> a.capacity() - b.capacity()).get();
	}

	private int getSizeTask(Collection<TaskAction> tasks) {
		return tasks.stream().map(t -> t.task).distinct().collect(Collectors.summingInt(t -> t.weight));
	}

	private void printMapVehicleListTask(Map<Vehicle, List<TaskAction>> map) {
		for (Map.Entry<Vehicle, List<TaskAction>> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			for (TaskAction t : entry.getValue())
				System.out.println("\t" + t.need_to_pickup + "\t" + t.task);
		}
	}

	private Map<Vehicle, List<TaskAction>> fillVehicules(Set<Vehicle> vehicles, Set<Task> tasks) {
		Map<Vehicle, List<TaskAction>> current = new HashMap<>();

		while (!vehicles.isEmpty() && !tasks.isEmpty()) {
			Vehicle current_vehicle = getBiggestVehicule(vehicles);
			List<TaskAction> current_tasks = new ArrayList<>();

			while (current_vehicle.capacity() > getSizeTask(current_tasks)) {
				Task t = tasks.iterator().next();
				tasks.remove(t);

				current_tasks.add(new TaskAction(t, true));
				current_tasks.add(new TaskAction(t, false));
			}

			current.put(current_vehicle, current_tasks);

			vehicles.remove(current_vehicle);
		}

		for (Vehicle v : vehicles)
			current.put(v, new ArrayList<>());

		return current;
	}

	private TaskAction nextTask(Map<Vehicle, List<TaskAction>> current, TaskAction prev) {
		for (List<TaskAction> tasks : current.values()) {
			boolean next = false;
			for (TaskAction t : tasks) {
				if (next)
					return t;
				next = t.equals(prev);
			}
		}

		return null;
	}

	private TaskAction nextTask(Map<Vehicle, List<TaskAction>> current, Vehicle v) {
		List<TaskAction> tasks = current.get(v);

		if (tasks == null || tasks.isEmpty())
			return null;

		return tasks.get(0);
	}

	private double dist(Task t, Task next) {
		if (next == null)
			return 0;

		return t.deliveryCity.distanceTo(next.pickupCity);
	}

	private double dist(Vehicle v, Task next) {
		if (next == null)
			return 0;

		return v.getCurrentCity().distanceTo(next.pickupCity);
	}

	private double length(Task t) {
		if (t == null)
			return 0;

		return t.pathLength();
	}

	private double cost(Vehicle v) {
		return v.costPerKm();
	}

	private Vehicle vehicle(Map<Vehicle, List<TaskAction>> current, TaskAction to_find) {
		for (Map.Entry<Vehicle, List<TaskAction>> entry : current.entrySet())
			for (TaskAction t : entry.getValue())
				if (t.equals(to_find))
					return entry.getKey();

		return null;
	}

	private double cost(Map<Vehicle, List<TaskAction>> m) {
		double realCost = 0.0;
		Map<Vehicle, Plan> plans = mapListTaskAsMapPlan(m);

		for (Vehicle v : m.keySet()) {
			Plan p = plans.get(v);
			realCost += p.totalDistance() * v.costPerKm();
		}

		return realCost;
	}

	private Set<TaskAction> getTasks(Map<Vehicle, List<TaskAction>> m) {
		return m.values().stream().flatMap(t -> t.stream()).collect(Collectors.toSet());
	}

	private static Random rand = new Random();

	private <T> T pickRandom(Collection<T> c) {
		int pos = rand.nextInt() % c.size();

		Iterator<T> iter = c.iterator();
		for (int i = 0; i < pos; ++i)
			iter.next();

		return iter.next();
	}

	private <T> Set<T> pickRandom(Collection<T> c, int size) {
		assert c.size() >= size;

		Set<T> to_ret = new HashSet<>();

		Set<Integer> to_select = new HashSet<>();
		while(to_select.size() < size)
			 to_select.add(Math.abs(rand.nextInt()) % c.size());
		assert to_select.size() == size;

		int i = 0;
		for (T t : c) {
			if (to_select.contains(i))
				to_ret.add(t);
			++i;
		}
		assert to_ret.size() == size;

		return to_ret;
	}

	private Map<Vehicle, List<TaskAction>> dupMap(Map<Vehicle, List<TaskAction>> m) {
		Map<Vehicle, List<TaskAction>> new_map = new HashMap<>();

		for (Map.Entry<Vehicle, List<TaskAction>> entry : m.entrySet()) {
			List<TaskAction> new_tasks = new ArrayList<>();
			for (TaskAction t : entry.getValue())
				new_tasks.add(t);
			new_map.put(entry.getKey(), new_tasks);
		}

		return new_map;
	}

	private Map<Vehicle, List<TaskAction>> removeTask(Map<Vehicle, List<TaskAction>> m, Task to_remove) {
		Map<Vehicle, List<TaskAction>> new_map = new HashMap<>();

		for (Map.Entry<Vehicle, List<TaskAction>> entry : m.entrySet()) {
			List<TaskAction> new_tasks = new ArrayList<>();
			for (TaskAction t : entry.getValue()) {
				if (!t.task.equals(to_remove))
					new_tasks.add(t);
			}

			new_map.put(entry.getKey(), new_tasks);
		}

		return new_map;
	}

	private Map<Vehicle, List<TaskAction>> moveTask(Map<Vehicle, List<TaskAction>> m) {
		Set<TaskAction> tasks = getTasks(m);
		TaskAction t = pickRandom(tasks);

		m = removeTask(m, t.task);
		Vehicle v = pickRandom(m.keySet());
		int pos;
		if (!m.get(v).isEmpty())
			pos = Math.abs(rand.nextInt()) % m.get(v).size();
		else
			pos = 0;
		m.get(v).add(pos, new TaskAction(t.task, true));
		m.get(v).add(pos + 1, new TaskAction(t.task, false));

		assert getTasks(m).size() == tasks.size();

		return m;
	}

	private boolean isConsistant(Map<Vehicle, List<TaskAction>> m) {
		for (List<TaskAction> actions : m.values()) {

			for (int i = 0; i < actions.size(); ++i) {
				TaskAction task_action = actions.get(i);

				if (!actions.contains(task_action.invert()))
					return false;

				if (task_action.need_to_pickup &&
					actions.indexOf(task_action.invert()) < i)
					return false;
				if (!task_action.need_to_pickup &&
					actions.indexOf(task_action.invert()) > i)
					return false;
			}
		}

		return true;
	}

	private Map<Vehicle, List<TaskAction>> swapTask(Map<Vehicle, List<TaskAction>> map) {
		Map<Vehicle, List<TaskAction>> m = dupMap(map);

		Vehicle v = pickRandom(m.keySet());
		List<TaskAction> tasks = m.get(v);

		if (tasks.size() < 2)
			return map;

		List<TaskAction> choices = new ArrayList<>(pickRandom(tasks, 2));

		Collections.swap(tasks, tasks.indexOf(choices.get(0)), tasks.indexOf(choices.get(1)));

		if (!isConsistant(m))
			return map;

		assert getTasks(map).size() == getTasks(m).size();

		return m;
	}

	private Map<Vehicle, List<TaskAction>> findBest(Set<Vehicle> vehicles, Set<Task> tasks) {
		Map<Vehicle, List<TaskAction>> best = fillVehicules(vehicles, tasks);

		Set<Function<Map<Vehicle, List<TaskAction>>,Map<Vehicle, List<TaskAction>>>> transformations = new HashSet<>();
		transformations.add(this::moveTask);
		transformations.add(this::swapTask);

		long step_start = System.currentTimeMillis();
		best = pickRandom(transformations).apply(best);
		long step_stop = System.currentTimeMillis();
		long step = step_stop - step_start;

		for (int stepWithoutBest = 0; stepWithoutBest < 100000 && step * 100 < getRemainingTime(); ++stepWithoutBest) {
			Map<Vehicle, List<TaskAction>> m = pickRandom(transformations).apply(best);

			if (cost(m) < cost(best)) {
				best = m;
				stepWithoutBest = 0;
			}
		}

		return best;
	}

	private City movePickupAndMoveDeliver(City current, Plan plan, TaskAction task_action) {
		Task task = task_action.task;

		if (task_action.need_to_pickup) {
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			return task.pickupCity;
		}

		for (City city : current.pathTo(task.deliveryCity))
			plan.appendMove(city);

		plan.appendDelivery(task);

		return task.deliveryCity;
	}

	private Map<Vehicle, Plan> mapListTaskAsMapPlan(Map<Vehicle, List<TaskAction>> map) {
		Map<Vehicle, Plan> res = new HashMap<>();

		for (Map.Entry<Vehicle, List<TaskAction>> entry : map.entrySet()) {
			Vehicle v = entry.getKey();
			City current = v.getCurrentCity();
			Plan p = new Plan(current);

			for(TaskAction t : entry.getValue())
				current = movePickupAndMoveDeliver(current, p, t);

			res.put(v, p);
		}

		return res;
	}

	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		this.time_start = System.currentTimeMillis();

		Map<Vehicle, List<TaskAction>> best = findBest(new HashSet<>(vehicles), new HashSet<Task>(tasks));
		Map<Vehicle, Plan> plans_best = mapListTaskAsMapPlan(best);

		List<Plan> plans = new ArrayList<Plan>();
		for (Vehicle v : vehicles)
			plans.add(plans_best.getOrDefault(v, Plan.EMPTY));

		long time_end = System.currentTimeMillis();
		long duration = time_end - time_start;
		System.out.println("The plan was generated in "+duration+" milliseconds.");
		System.out.println("The cost is " + cost(best));
		printMapVehicleListTask(best);

		return plans;
	}

	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity)) {
				plan.appendMove(city);
			}

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path()) {
				plan.appendMove(city);
			}

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}
}
