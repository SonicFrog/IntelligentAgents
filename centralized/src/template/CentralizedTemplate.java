package template;

//the list of imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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

		return current;
	}

	private Map<Vehicle, List<Task>> findBest(Set<Vehicle> vehicles, Set<Task> tasks) {
		Map<Vehicle, List<Task>> current = fillVehicules(vehicles, tasks);

		return current;
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
