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

	private Vehicle getBiggestVehicule(Set<Vehicle> vehicles) {
		return vehicles.stream().max((a, b) -> a.capacity() - b.capacity()).get();
	}

	private int getSizeTask(Collection<Task> tasks) {
		return tasks.stream().collect(Collectors.summingInt(t -> t.weight));
	}

	private void printMapVehicleListTask(Map<Vehicle, List<Task>> map) {
		for (Map.Entry<Vehicle, List<Task>> entry : map.entrySet()) {
			System.out.println(entry.getKey());
			for (Task t : entry.getValue())
				System.out.println("\t" + t);
		}
	}

	private Map<Vehicle, List<Task>> fillVehicules(Set<Vehicle> vehicles, Set<Task> tasks) {
		Map<Vehicle, List<Task>> current = new HashMap<>();

		while (!vehicles.isEmpty() && !tasks.isEmpty()) {
			Vehicle current_vehicle = getBiggestVehicule(vehicles);
			List<Task> current_tasks = new ArrayList<>();

			while (current_vehicle.capacity() > getSizeTask(current_tasks)) {
				Task t = tasks.iterator().next();
				tasks.remove(t);

				current_tasks.add(t);
			}

			current.put(current_vehicle, current_tasks);

			vehicles.remove(current_vehicle);
		}

		for (Vehicle v : vehicles)
			current.put(v, new ArrayList<>());

		return current;
	}

	private Task nextTask(Map<Vehicle, List<Task>> current, Task prev) {
		for (List<Task> tasks : current.values()) {
			boolean next = false;
			for (Task t : tasks) {
				if (next)
					return t;
				next = t.equals(prev);
			}
		}

		return null;
	}

	private Task nextTask(Map<Vehicle, List<Task>> current, Vehicle v) {
		List<Task> tasks = current.get(v);

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

	private Vehicle vehicle(Map<Vehicle, List<Task>> current, Task to_find) {
		for (Map.Entry<Vehicle, List<Task>> entry : current.entrySet())
			for (Task t : entry.getValue())
				if (t.equals(to_find))
					return entry.getKey();

		return null;
	}

	private double cost(Map<Vehicle, List<Task>> m) {
		double res = 0;

		for (Map.Entry<Vehicle, List<Task>> entry : m.entrySet()) {
			Vehicle v = entry.getKey();
			List<Task> tasks = entry.getValue();

			res += tasks.stream()
				.mapToDouble((t -> (dist(t, nextTask(m, t)) + length(nextTask(m, t))) * cost(vehicle(m, t))))
				.sum();
			res += (dist(v, nextTask(m, v)) + length(nextTask(m, v))) * cost(v);
		}

		return res;
	}

	private Set<Task> getTasks(Map<Vehicle, List<Task>> m) {
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

	private Map<Vehicle, List<Task>> dupMap(Map<Vehicle, List<Task>> m) {
		Map<Vehicle, List<Task>> new_map = new HashMap<>();

		for (Map.Entry<Vehicle, List<Task>> entry : m.entrySet()) {
			List<Task> new_tasks = new ArrayList<>();
			for (Task t : entry.getValue())
				new_tasks.add(t);
			new_map.put(entry.getKey(), new_tasks);
		}

		return new_map;
	}

	private Map<Vehicle, List<Task>> removeTask(Map<Vehicle, List<Task>> m, Task to_remove) {
		Map<Vehicle, List<Task>> new_map = new HashMap<>();

		for (Map.Entry<Vehicle, List<Task>> entry : m.entrySet()) {
			List<Task> new_tasks = new ArrayList<>();
			for (Task t : entry.getValue()) {
				if (!t.equals(to_remove))
					new_tasks.add(t);
			}

			new_map.put(entry.getKey(), new_tasks);
		}

		return new_map;
	}

	private Map<Vehicle, List<Task>> moveTask(Map<Vehicle, List<Task>> m) {
		Set<Task> tasks = getTasks(m);
		Task t = pickRandom(tasks);

		m = removeTask(m, t);
		Vehicle v = pickRandom(m.keySet());
		int pos;
		if (!m.get(v).isEmpty())
			pos = Math.abs(rand.nextInt()) % m.get(v).size();
		else
			pos = 0;
		m.get(v).add(pos, t);

		assert getTasks(m).size() == tasks.size();

		return m;
	}

	private Map<Vehicle, List<Task>> swapTask(Map<Vehicle, List<Task>> map) {
		Map<Vehicle, List<Task>> m = dupMap(map);

		Vehicle v = pickRandom(m.keySet());
		List<Task> tasks = m.get(v);

		if (tasks.size() < 2)
			return m;

		List<Task> choices = new ArrayList<>(pickRandom(tasks, 2));

		Collections.swap(tasks, tasks.indexOf(choices.get(0)), tasks.indexOf(choices.get(1)));

		assert getTasks(map).size() == getTasks(m).size();

		return m;
	}

	private Map<Vehicle, List<Task>> findBest(Set<Vehicle> vehicles, Set<Task> tasks) {
		Map<Vehicle, List<Task>> best = fillVehicules(vehicles, tasks);

		Set<Function<Map<Vehicle, List<Task>>,Map<Vehicle, List<Task>>>> transformations = new HashSet<>();
		transformations.add(this::moveTask);
		transformations.add(this::swapTask);

		for (int stepWithoutBest = 0; stepWithoutBest < 100000; ++stepWithoutBest) {
			Map<Vehicle, List<Task>> m = pickRandom(transformations).apply(best);

			if (cost(m) < cost(best)) {
				best = m;
				stepWithoutBest = 0;
			}
		}

		return best;
	}

	private City movePickupAndMoveDeliver(City current, Plan plan, Task task) {
		for (City city : current.pathTo(task.pickupCity))
			plan.appendMove(city);

		plan.appendPickup(task);

		for (City city : task.path())
			plan.appendMove(city);

		plan.appendDelivery(task);

		return task.deliveryCity;
	}

	private Map<Vehicle, Plan> mapListTaskAsMapPlan(Map<Vehicle, List<Task>> map) {
		Map<Vehicle, Plan> res = new HashMap<>();

		for (Map.Entry<Vehicle, List<Task>> entry : map.entrySet()) {
			Vehicle v = entry.getKey();
			City current = v.getCurrentCity();
			Plan p = new Plan(current);

			for(Task t : entry.getValue())
				current = movePickupAndMoveDeliver(current, p, t);

			res.put(v, p);
		}

		return res;
	}

	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		long time_start = System.currentTimeMillis();

		Map<Vehicle, List<Task>> best = findBest(new HashSet<>(vehicles), new HashSet<Task>(tasks));
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
