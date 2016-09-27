package ch.epfl.ia;

import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author Ogier & Valérian
 */

public class RabbitsGrassSimulationAgent implements Drawable {

    private static int nextID = 0;

    private int ID;

    private int money;

    private int stepsToLive;

    private Position position;

    public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
        position = new Position(-1, -1);
        money = 0;
        stepsToLive = (int) ((Math.random() * (maxLifespan - minLifespan))
                             + minLifespan);
        ID = nextID++;
    }

    public void step() {
        stepsToLive--;
    }

    public void setPosition(Position pos) {
        position = pos;
    }

    public String getID() {
        return "A-" + ID;
    }

    public int getMoney() {
        return money;
    }

    public int getSTL() {
        return stepsToLive;
    }

    public void report() {
        System.out.println(getID() + " at " + position.x + ", " + position.y
                           + " has " + getMoney() + " dollars and " + getSTL() +
                           " steps to live");

    }

    public void draw(SimGraphics arg0) {
        arg0.drawFastRoundRect(Color.blue);
    }

    public Position getPosition() {
        return position;
    }

    public int getX() {
        return position.x();
    }

    public int getY() {
        return position.y();
    }
}
