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
 * @author Ogier & Valérian
 */
public class RabbitsGrassSimulationModel extends SimModelImpl {

    private Schedule schedule;

    private static final int NUMAGENTS = 100;
    private static final int WORLDXSIZE = 40;
    private static final int WORLDYSIZE = 40;

    private int numAgents = NUMAGENTS;

    private int worldXSize = WORLDXSIZE, worldYSize = WORLDYSIZE;

    private RabbitsGrassSimulationSpace space;

    public static void main(String[] args) {
        SimInit init = new SimInit();
        RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
        init.loadModel(model, "", false);
    }

    public void setup() {
        System.out.println("Running setup");
        space = null;
    }

    public void begin() {
        buildModel();
        buildSchedule();
        buildDisplay();
    }

    public void buildModel() {
        System.out.println("Running buildModel");
        space = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
    }

    public void buildSchedule() {
        System.out.println("Running buildSchedule");
    }

    public void buildDisplay() {
        System.out.println("Running buildDisplay");
    }

    public String[] getInitParam() {
        String[] params = { "NumAgents", "WorldXSize", "WorldYsize" };
        return params;
    }

    public String getName() {
        return "Rabbits simulation model";
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setWorldXSize(int x) {
        worldXSize = x;
    }

    public int getWorldXSize() {
        return worldXSize;
    }

    public void setWorldYSize(int y) {
        worldYSize = y;
    }

    public int getWorldYSize() {
        return worldYSize;
    }

    public void setNumAgents(int a) {
        this.numAgents = a;
    }

    public int getNumAgents() {
        return numAgents;
    }
}
