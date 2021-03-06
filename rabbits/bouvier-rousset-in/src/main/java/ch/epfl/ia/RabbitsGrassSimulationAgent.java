package ch.epfl.ia;

import java.awt.Color;
import ch.epfl.ia.RandomUtil;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author Ogier & Valérian
 */

public class RabbitsGrassSimulationAgent implements Drawable {

    private static int nextID = 0;

    private int energy = 0;

    private int ID;

    private int birthThreshold;

    private Position position;

    private RabbitsGrassSimulationSpace space;

    private int vX, vY;

    private int agingRate;

    private int deathThreshold;

    public RabbitsGrassSimulationAgent(int birthThreshold, int agingRate,
                                       int deathThreshold, int initEnergy) {
        position = new Position(-1, -1);
        ID = nextID++;
        this.energy = initEnergy;
        this.agingRate = agingRate;
        this.birthThreshold = birthThreshold;
        this.deathThreshold = deathThreshold;
    }

    public void setAgentSpace(RabbitsGrassSimulationSpace space) {
        this.space = space;
    }

    /**
     * Checks if this agent is dead
     **/
    public boolean isDead() {
        return energy < deathThreshold;
    }

    /**
     * Computes move direction
     **/
    private void setVxVy() {
        vX = 0;
        vY = 0;

        int delta = RandomUtil.randomInt(1) * 2 - 1;
        if (RandomUtil.randomInt(1) == 0)
            vX = delta;
        else
            vY = delta;

        assert ((vX == -1 || vX == 1) && vY == 0) ||
            (vX == 0 && (vY == -1 || vY == 1));
    }

    private boolean willMove() {
        return RandomUtil.randomInt(2) == 1;
    }

    /**
     * Evaluates one step of simulation for this agent
     **/
    public void step() {
        Object2DGrid grid = space.getCurrentAgentSpace();

        int newX = Math.floorMod(position.x + vX, grid.getSizeX());
        int newY = Math.floorMod(position.y + vY, grid.getSizeY());

        if (willMove())
            tryMove(newX, newY);

        eatGrass();
        age();
        setVxVy();
    }

    private void age() {
        System.out.println("Current rabbit has " + energy + " and spends " + agingRate);
        energy -= agingRate;
    }

    /**
     * Tries to give birth to a new rabbit
     **/
    public boolean tryGiveBirth() {
        if (energy >= birthThreshold) {
            energy -= birthThreshold;

            System.out.println("Gave birth to a new rabbit!");

            return true;
        }
        return false;
    }

    /**
     * This agent eats the grass at its current position
     **/
    public void eatGrass() {
        energy += space.eatGrassAt(position.x, position.y);
    }

    /**
     * Try to move this agent to a new position
     * @param newX The new X coordinate
     * @param newY The new Y coordinate
     **/
    public boolean tryMove(int newX, int newY) {
        return space.moveAgentAt(position.x, position.y, newX, newY);
    }

    public String getID() {
        return "A-" + ID;
    }

    /**
     * Prints out a complete report for this agent
     **/
    public void report() {
        System.out.println(getID() + " at " + position.x + ", " + position.y
                           + " has " + energy + " steps to live");
    }

    @Override
    public void draw(SimGraphics arg0) {
        if (10 < energy && energy < 30)
            arg0.drawFastRoundRect(Color.yellow);
        else if (30 <= energy && energy < 60)
            arg0.drawFastRoundRect(Color.green);
        else
            arg0.drawFastRoundRect(Color.blue);
    }

    public int getEnergy() {
        return energy;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        position = pos;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }
}
