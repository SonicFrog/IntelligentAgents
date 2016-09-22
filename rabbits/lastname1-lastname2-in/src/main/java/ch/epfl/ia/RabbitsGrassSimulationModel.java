package ch.epfl.ia;

import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author Ogier
 */
public class RabbitsGrassSimulationModel extends SimModelImpl {

    private Schedule schedule;
    private int numAgents;

    private int worldXSize, worldYSize;

    public static void main(String[] args) {
        SimInit init = new SimInit();
        RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
        init.loadModel(model, "", false);
    }

    public void begin() {
        // TODO: stub
    }

    public void setup() {
        // TODO: stub
    }

    public void buildModel() {
        // TODO: stub
    }

    public void buildSchedule() {
        // TODO: stub
    }

    public void buildDisplay() {
        // TODO: stub
    }

    public String[] getInitParam() {
        String[] params = { "NumAgents", "WorldXSize", "WorldYsize" };
        return null;
    }

    public String getName() {
        return "Rabbits simulation model";
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setWorldXSize(int x) {
        dimX = x;
    }

    public int getWorldXSize() {
        return dimX;
    }

    public void setWorldYSize(int y) {
        dimY = y;
    }

    public int getWorldYSize() {
        return dimY;
    }

    public void setNumAgents(int a) {
        this.numAgents = a;
    }

    public int getNumAgents() {
        return numAgents;
    }
}
