package ch.epfl.ia;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import java.awt.Dimension;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

    private int x;
    private int y;

    private int money;

    private int stepsToLive;

    private Dimension position;

    public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
        x = -1;
        y = -1;
        money = 0;
        stepsToLive = (int) ((Math.random() * (maxLifespan - minLifespan))
                             + minLifespan);
    }

    public void setPosition(Dimension pos) {
        position = pos;
    }

    public void draw(SimGraphics arg0) {

    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

}
