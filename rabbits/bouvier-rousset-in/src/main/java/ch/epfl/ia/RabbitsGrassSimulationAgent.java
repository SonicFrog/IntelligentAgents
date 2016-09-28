package ch.epfl.ia;

import java.awt.Color;

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

    private int stepsToLive;

    private Position position;

    private RabbitsGrassSimulationSpace space;

    private int vX, vY;

    public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
        position = new Position(-1, -1);
        stepsToLive = (int) ((Math.random() * (maxLifespan - minLifespan))
                             + minLifespan);
        ID = nextID++;
    }

    public void setAgentSpace(RabbitsGrassSimulationSpace space) {
        this.space = space;
    }

    /**
     * Checks if this agent is dead
     **/
    public boolean isDead() {
        return stepsToLive < 1;
    }

    /**
     * Computes move direction
     **/
    private void setVxVy() {
        vX = 0;
        vY = 0;

        while ((vX == 0) && (vY == 0)) {
            if (RandomUtil.randomInt(1) == 0)
                vX = RandomUtil.randomInt(-1, 1);
            else
                vY = RandomUtil.randomInt(-1, 1);
        }
    }

    /**
     * Evaluates one step of simulation for this agent
     **/
    public void step() {
        Object2DGrid grid = space.getCurrentAgentSpace();

        int newX = (position.x() + vX) % grid.getSizeX();
        int newY = (position.y() + vY) % grid.getSizeY();

        if (tryMove(newX, newY)) {
            eatGrass();
        }

        stepsToLive--;
        setVxVy();
    }

    /**
     * This agent eats the grass at its current position
     **/
    private void eatGrass() {
        energy += space.eatGrassAt(position.x(), position.y());
    }

    /**
     * Try to move this agent to a new position
     * @param newX The new X coordinate
     * @param newY The new Y coordinate
     **/
    private boolean tryMove(int newX, int newY) {
        return space.moveAgentAt(position.x(), position.y(), newX, newY);
    }

    public String getID() {
        return "A-" + ID;
    }

    /**
     * Get remaining living time for this agent
     **/
    public int getSTL() {
        return stepsToLive;
    }

    /**
     * Prints out a complete report for this agent
     **/
    public void report() {
        System.out.println(getID() + " at " + position.x + ", " + position.y
                           + " has " + " and " + getSTL() + " steps to live");
    }

    /**
     * Makes this rabbit give birth if able
     **/
    public boolean giveBirth(int threshold) {
        boolean res = energy >= threshold;

        if (res) {
            this.energy -= threshold;
        }

        return res;
    }

    @Override
    public void draw(SimGraphics arg0) {
        if (stepsToLive > 10)
            arg0.drawFastRoundRect(Color.yellow);
        else if (stepsToLive == 0)
            arg0.drawFastRoundRect(Color.black);
        else
            arg0.drawFastRoundRect(Color.blue);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        position = pos;
    }

    public int getX() {
        return position.x();
    }

    public int getY() {
        return position.y();
    }
}
