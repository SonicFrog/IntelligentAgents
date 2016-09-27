package ch.epfl.ia;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.util.SimUtilities;

import uchicago.src.sim.engine.BasicAction;
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

    private List<RabbitsGrassSimulationAgent> agents;


    public static void main(String[] args) {
        SimInit init = new SimInit();
        RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
        init.loadModel(model, "", false);
    }

    /**
     * Sets up all required components of simulation
     **/
    @Override
    public void setup() {
        System.out.println("Running setup");
        space = null;

        schedule = new Schedule(1);

        agents = new ArrayList<>();

        if (surface != null) {
            surface.dispose();
        }
        surface = null;
        surface = new DisplaySurface(this, "Carry drop model window 1");
        this.registerDisplaySurface("Carry drop model window1", surface);
    }

    @Override
    public void begin() {
        buildModel();
        buildSchedule();
        buildDisplay();

        surface.display();
    }

    /**
     * Builds the model and initializes the simulation space
     **/
    public void buildModel() {
        System.out.println("Running buildModel");
        space = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
        space.spreadMoney(TOTAL_MONEY);

        for (int i = 0; i < numAgents; i++) {
            addNewAgent();
        }

        for (RabbitsGrassSimulationAgent a : agents) {
            a.report();
        }
    }

    /**
     * Builds the simulation schedule during setup
     **/
    public void buildSchedule() {
        System.out.println("Running buildSchedule");

        class RabbitsGrassSimulationStep extends BasicAction {
            @Override
            public void execute() {
                SimUtilities.shuffle(agents);
                for (RabbitsGrassSimulationAgent a : agents) {
                    a.step();
                }

                int deadRabbitsCount = eatDeadStreamRabbits();

                System.out.println("Found " + deadRabbitsCount + " dead rabbits!");

                for (int i = 0; i < deadRabbitsCount; i++ ) {
                    addNewAgent();
                }

                surface.updateDisplay();
            }
        }

        class RabbitsGrassSimulationCountLiving extends BasicAction {
            @Override
            public void execute() {
                System.out.println("There are " + countLivingAgents() + " living agents!");
            }
        }

        schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());
        schedule.scheduleActionAtInterval(10, new RabbitsGrassSimulationCountLiving());
    }

    /**
     * Creates the display components of the simulation
     **/
    public void buildDisplay() {
        System.out.println("Running buildDisplay");

        ColorMap map = new ColorMap();

        for (int i = 1; i < 16; i++) {
            map.mapColor(i, new Color((int) i * 8 + 127, 0, 0));
        }
        map.mapColor(0, Color.WHITE);

        Value2DDisplay displayMoney =
            new Value2DDisplay(space.getCurrentMoneySpace(), map);

        Object2DDisplay agentDisplay = new Object2DDisplay(space.getCurrentAgentSpace());
        agentDisplay.setObjectList(agents);

        surface.addDisplayable(displayMoney, "Money display");
        surface.addDisplayable(agentDisplay, "Agent display");
    }

    /**
     * Generates a new random agent and adds to the simulation space
     **/
    private void addNewAgent() {
        RabbitsGrassSimulationAgent a =
            new RabbitsGrassSimulationAgent(agentMinLifespan, agentMaxLifespan);
        agents.add(a);
        space.addAgent(a);
    }

    public int countLivingAgents() {
        // Java lambdas yay!
        return (int) agents.stream().filter(a -> !a.isDead()).count();
    }

    /**
     * Use this if you're hungry
     **/
    private int eatDeadStreamRabbits() {
        List<RabbitsGrassSimulationAgent> dead = agents.stream()
            .filter(a -> a.isDead()).collect(Collectors.toList());

        dead.stream().forEach(a ->
                              agents.remove(a));

        return (int) dead.stream().count();

    }

    @Override
    public String[] getInitParam() {
        String[] params = { "NumAgents", "WorldXSize", "WorldYsize",
                            "AgentMinLifespan", "AgentMaxLifespan" };
        return params;
    }

    @Override
    public String getName() {
        return "Rabbits simulation model";
    }

    @Override
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
