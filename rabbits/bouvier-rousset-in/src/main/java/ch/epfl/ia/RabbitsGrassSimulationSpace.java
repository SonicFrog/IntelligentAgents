package ch.epfl.ia;

import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author Ogier & Valérian
 */

public class RabbitsGrassSimulationSpace {
    private Object2DGrid space;

    public RabbitsGrassSimulationSpace(int xSize, int ySize) {
        space = new Object2DGrid(xSize, ySize);

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                space.putObjectAt(i, j, new Integer(0));
            }
        }
    }
}
