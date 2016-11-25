package template;

//the list of imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.util.stream.Collectors;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class AuctionCentralized implements AuctionBehavior {

	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private Random random;
	private List<Vehicle> vehicles;
	private Map<Vehicle, City> currentCities;
	private double increment;

	@Override
	public void setup(Topology topology, TaskDistribution distribution,
			Agent agent) {

		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
		this.vehicles = agent.vehicles();
		this.currentCities = this.vehicles.stream()
			.collect(Collectors.toMap(v -> v, v -> v.homeCity()));

		long seed = -9019554669489983951L;
		this.random = new Random(seed);
		this.increment = 1;

		System.out.println("~~ " + this.agent.id());
	}

	private long getSecondLowestBid(Long[] bids, int winner) {
		return Arrays.stream(bids)
			.filter(v -> v > bids[winner])
			.mapToLong(t -> t).min().getAsLong();
	}

	@Override
	public void auctionResult(Task task, int winner, Long[] bids) {
		System.out.println(">> " + task);
		System.out.println(">> " + winner);
		for (Long b : bids)
			System.out.println(b);

		if (winner != agent.id()) {
			increment *= 0.5;
			return;
		}

		increment = Math.max(increment, getSecondLowestBid(bids, winner) - bids[winner] - 1);

		Vehicle v = getBestVehiculeForTask(task);
		this.currentCities.put(v, task.deliveryCity);
	}

	private double getCostToTask(Vehicle v, Task t) {
		return Measures.unitsToKM(
			currentCities.get(v).distanceUnitsTo(t.pickupCity)
			+ t.pickupCity.distanceUnitsTo(t.deliveryCity)) * v.costPerKm();
	}

	private <T> T pickOne(Collection<T> col) {
		int pos = this.random.nextInt() % col.size();

		int i = 0;
		for (T e : col) {
			if (i == pos)
				return e;
			++i;
		}

		return null;
	}

	private Vehicle getBestVehiculeForTask(Task t) {
		Set<Vehicle> remains = this.vehicles.stream().filter(v -> t.weight <= v.capacity())
			.collect(Collectors.toSet());

		if (remains.size() == 1)
			return pickOne(remains);

		Map<Vehicle, Double> costForNextTask = this.vehicles.stream()
			.collect(Collectors.toMap(v -> v, v -> getCostToTask(v, t)));

		double min = Double.MAX_VALUE;
		Vehicle best = null;
		for (Map.Entry<Vehicle, Double> entry : costForNextTask.entrySet()) {
			if (entry.getValue() < min) {
				min = entry.getValue();
				best = entry.getKey();
			}
		}

		return best;
	}



	@Override
	public Long askPrice(Task task) {
		Vehicle v = getBestVehiculeForTask(task);
		double cost = getCostToTask(v, task);

		double bid = cost + increment; // TODO better?

		return Math.round(bid);
	}

	private double getTotalReward(TaskSet tasks) {
		return tasks.stream().mapToDouble(t -> t.reward).sum();
	}

	private double getTotalCost(List<Plan> plans, List<Vehicle> vehicles) {
		double acc = 0;

		for (int i = 0; i < plans.size(); ++i)
			acc += plans.get(i).totalDistance() * vehicles.get(i).costPerKm();

		return acc;
	}

	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		CentralizedPlan cp = new CentralizedPlan();
		List<Plan> p = cp.plan(vehicles, tasks);
		System.out.println("total reward: " + (getTotalReward(tasks) - getTotalCost(p, vehicles)));
		return p;
	}
}
