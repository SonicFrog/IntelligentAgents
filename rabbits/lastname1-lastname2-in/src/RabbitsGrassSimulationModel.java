import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {

    private Schedule schedule;
    private int numAgents;

    public static void main(String[] args) {

        System.out.println("Rabbit skeleton");

    }

    public void begin() {
        // TODO Auto-generated method stub

    }

    public void setup() {
        // TODO Auto-generated method stub

    }

    public void buildModel() {

    }

    public void buildSchedule() {

    }

    public void buildDisplay() {

    }

    public String[] getInitParam() {
        String[] params = { "NumAgents", "AgentStrength" };
        return null;
    }

    public String getName() {
        return "Rabbits simulation model";
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setNumAgents(int a) {
        this.numAgents = a;
    }

    public int getNumAgents() {
        return numAgents;
    }
}
