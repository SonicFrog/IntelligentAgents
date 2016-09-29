package ch.epfl.ia;

import java.awt.Dimension;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author Ogier & Valérian
 */

public class RabbitsGrassSimulationSpace {
    private Object2DGrid grassSpace;
    private Object2DGrid agentSpace;

    public RabbitsGrassSimulationSpace(int xSize, int ySize) {
        grassSpace = new Object2DGrid(xSize, ySize);
        agentSpace = new Object2DGrid(xSize, ySize);

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                grassSpace.putObjectAt(i, j, new Integer(0));
            }
        }
    }

    public void removeAgentAt(int x, int y) {
        agentSpace.putObjectAt(x, y, null);
    }

    public Object2DGrid getCurrentGrassSpace() {
        return grassSpace;
    }

    public Object2DGrid getCurrentAgentSpace() {
        return agentSpace;
    }

    public boolean isCellOccupied(int x, int y) {
        return agentSpace.getObjectAt(x, y) != null;
    }

    private Set<Position> getEmptyCells() {
        Set<Position> set = new HashSet<Position>();

        for(int x = agentSpace.getSizeX() - 1; x >= 0; --x)
            for(int y = agentSpace.getSizeY() - 1; y >= 0; --y)
                if(!isCellOccupied(x, y))
                    set.add(new Position(x, y));

        return set;
    }

    public boolean addAgent(RabbitsGrassSimulationAgent agent) {
        List<Position> cells = new ArrayList<Position>(getEmptyCells());
        if(cells.size() == 0) {
            System.out.println("Fail to add new rabbit");
            return false;
        }

        Collections.shuffle(cells);
        Position newPos = cells.get(0);

        agentSpace.putObjectAt(newPos.x, newPos.y, agent);
        agent.setPosition(newPos);
        agent.setAgentSpace(this);

        return true;
    }

    public boolean moveAgentAt(int x, int y, int newX, int newY) {
        boolean res = isCellOccupied(newX, newY);

        if (!res) {
            RabbitsGrassSimulationAgent agent =
                (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
            removeAgentAt(x, y);
            agent.setPosition(new Position(newX, newY));
            agentSpace.putObjectAt(newX, newY, agent);
        }

        return res;
    }

    /**
     * Grows the grass by amount at given position
     **/
    public void growGrassAt(int x, int y, int amount) {
        Integer updated = getGrassAt(x, y) + amount;

        grassSpace.putObjectAt(x, y, updated);
    }

    /**
     * Gets the grass level at (x, y)
     **/
    public int getGrassAt(int x, int y) {
        Integer i = (Integer) grassSpace.getObjectAt(x, y);

        return (i == null) ? 0 : i;
    }

    /**
     * Gets and resets the grass level at (x, y)
     **/
    public int eatGrassAt(int x, int y) {
        Integer value = getGrassAt(x, y);

        grassSpace.putObjectAt(x, y, 0);

        return value;
    }

    /**
     * Gets the rabbit at position (x, y)
     **/
    public RabbitsGrassSimulationAgent getAgentAt(int x, int y) {
        return (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
    }
}
