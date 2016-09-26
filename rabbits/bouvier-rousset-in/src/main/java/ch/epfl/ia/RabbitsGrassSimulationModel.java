package ch.epfl.ia;

import java.awt.Color;

import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;

import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.gui.DisplaySurface;


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
    private static final int TOTAL_MONEY = 200;
    private static final int AGENT_MIN_LIFESPAN = 30;
    private static final int AGENT_MAX_LIFESPAN = 50;


    private int numAgents = NUMAGENTS;
    private int worldXSize = WORLDXSIZE;
    private int worldYSize = WORLDYSIZE;
    private int agentMinLifespan = AGENT_MIN_LIFESPAN;
    private int agentMaxLifespan = AGENT_MAX_LIFESPAN;


    private RabbitsGrassSimulationSpace space;

    private DisplaySurface surface;

    public static void main(String[] args) {
        SimInit init = new SimInit();
        RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
        init.loadModel(model, "", false);
    }

    public void setup() {
        System.out.println("Running setup");
        space = null;

        if (surface != null) {
            surface.dispose();
        }
        surface = null;
        surface = new DisplaySurface(this, "Carry drop model window 1");
        this.registerDisplaySurface("Carry drop model window1", surface);
    }

    public void begin() {
        buildModel();
        buildSchedule();
        buildDisplay();

        surface.display();
    }

    public void buildModel() {
        System.out.println("Running buildModel");
        space = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
        space.spreadMoney(TOTAL_MONEY);
    }

    public void buildSchedule() {
        System.out.println("Running buildSchedule");
    }

    public void buildDisplay() {
        System.out.println("Running buildDisplay");

        ColorMap map = new ColorMap();

        for (int i = 1; i < 16; i++) {
            map.mapColor(i, new Color((int) i * 8 + 127, 0, 0));
        }
        map.mapColor(0, Color.WHITE);

        Value2DDisplay displayMoney =
            new Value2DDisplay(space.getCurrentSpace(), map);

        surface.addDisplayable(displayMoney, "Money display");
    }

    public String[] getInitParam() {
        String[] params = { "NumAgents", "WorldXSize", "WorldYsize",
                            "AgentMinLifespan", "AgentMaxLifespan" };
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

    public int getAgentMinLifespan() {
        return this.agentMinLifespan;
    }

    public void setAgentMinLifespan(int i) {
        this.agentMinLifespan = i;
    }

    public int getAgentMaxLifespan() {
        return agentMaxLifespan;
    }

    public void setAgentMaxLifespan(int i) {
        agentMaxLifespan = i;
    }
}
