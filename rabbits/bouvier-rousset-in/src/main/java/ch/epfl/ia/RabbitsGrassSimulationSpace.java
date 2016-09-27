package ch.epfl.ia;

import java.awt.Dimension;

import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author Ogier & Valérian
 */

public class RabbitsGrassSimulationSpace {
    private Object2DGrid moneySpace;
    private Object2DGrid agentSpace;

    public RabbitsGrassSimulationSpace(int xSize, int ySize) {
        moneySpace = new Object2DGrid(xSize, ySize);
        agentSpace = new Object2DGrid(xSize, ySize);

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                moneySpace.putObjectAt(i, j, new Integer(0));
            }
        }
    }

    public void spreadMoney(int total) {
        for (int i = 0; i < total; i++) {
            int x = (int) (Math.random() * moneySpace.getSizeX());
            int y = (int) (Math.random() * moneySpace.getSizeY());

            int currentValue = getMoneyAt(x, y);
            moneySpace.putObjectAt(x, y, new Integer(currentValue + 1));
        }
    }

    public int getMoneyAt(int x, int y) {
        int i;

        Integer current = (Integer) moneySpace.getObjectAt(x, y);

        if (current == null) {
            i = 0;
        } else {
            i = current.intValue();
        }

        return i;
    }

    public void removeAgentAt(int x, int y) {
        agentSpace.putObjectAt(x, y, null);
    }

    public Object2DGrid getCurrentMoneySpace() {
        return moneySpace;
    }

    public Object2DGrid getCurrentAgentSpace() {
        return agentSpace;
    }

    public boolean isCellOccupied(int x, int y) {
        return agentSpace.getObjectAt(x, y) != null;
    }

    public boolean addAgent(RabbitsGrassSimulationAgent agent) {
        boolean res = false;
        int count = 0;
        int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();

        while (!res && (count < countLimit)) {
            int x = (int) (Math.random() * agentSpace.getSizeX());
            int y = (int) (Math.random() * agentSpace.getSizeY());
            res = !isCellOccupied(x, y);

            if (res) {
                agentSpace.putObjectAt(x, y, agent);
                agent.setPosition(new Position(x, y));
            }

            count++;
        }

        return res;
    }
}
