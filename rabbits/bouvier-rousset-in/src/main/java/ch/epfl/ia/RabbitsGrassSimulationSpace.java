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

    public void spreadMoney(int total) {
        for (int i = 0; i < total; i++) {
            int x = (int) (Math.random() * space.getSizeX());
            int y = (int) (Math.random() * space.getSizeY());

            int currentValue = getMoneyAt(x, y);
            space.putObjectAt(x, y, new Integer(currentValue + 1));
        }
    }

    public int getMoneyAt(int x, int y) {
        int i;

        Integer current = (Integer) space.getObjectAt(x, y);

        if (current == null) {
            i = 0;
        } else {
            i = current.intValue();
        }

        return i;
    }
}
