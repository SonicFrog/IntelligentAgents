package ch.epfl.ia;

import ch.epfl.ia.RabbitsGrassSimulationAgent;
import ch.epfl.ia.RabbitsGrassSimulationSpace;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Test suite for the non-random agent behavior
 **/
public class TestAgentBehavior {

    public static final int INIT_ENERGY = 39;
    public static final int AGING_RATE = 4;
    public static final int DEATH_THRES = 0;
    public static final int BIRTH_THRES = 50;
    public static final int X_SIZE = 20;
    public static final int Y_SIZE = 20;
    public static final int GROWTH = 40;

    public RabbitsGrassSimulationAgent createAgent() {
        return new RabbitsGrassSimulationAgent(
            BIRTH_THRES, AGING_RATE, DEATH_THRES, INIT_ENERGY);
    }

    /**
     * Tests that the agent dies after the correct number of step
     **/
    @Test
    public void testAgentDies() throws Exception {
        RabbitsGrassSimulationSpace space =
            new RabbitsGrassSimulationSpace(X_SIZE, Y_SIZE);
        RabbitsGrassSimulationAgent agent = new RabbitsGrassSimulationAgent(
            BIRTH_THRES, AGING_RATE, DEATH_THRES, INIT_ENERGY);

        agent.setAgentSpace(space);
        space.addAgent(agent);

        double stepCount = Math.ceil(INIT_ENERGY / AGING_RATE) + 1;

        for (double i = 0; i < stepCount; i++) {
            assertFalse("Agent died too soon!", agent.isDead());
            agent.step();
        }

        assertTrue("Agent did not die in time!", agent.isDead());
    }

    /**
     * Tests that the agent correctly eats grass when moving to a new cell
     **/
    @Test
    public void testAgentEats() throws Exception {
        RabbitsGrassSimulationSpace space =
            new RabbitsGrassSimulationSpace(X_SIZE, Y_SIZE);
        RabbitsGrassSimulationAgent agent = createAgent();

        agent.setAgentSpace(space);
        space.growGrassAt(0, 0, GROWTH);
        assertTrue("Agent was not added to space!", space.addAgent(agent));
        space.moveAgentAt(agent.getPosition().x, agent.getPosition().y, 0, 0);
        agent.eatGrass();

        int correctEnergy = GROWTH + INIT_ENERGY;

        assertEquals("The agent did not have the correct amount of energy",
                     correctEnergy, agent.getEnergy());
    }

    /**
     * Tests that an agent can't move to an occupied cell
     **/
    @Test
    public void testMoveToOccupiedCell() throws Exception {
        RabbitsGrassSimulationSpace space =
            new RabbitsGrassSimulationSpace(X_SIZE, Y_SIZE);
        RabbitsGrassSimulationAgent agent1 = createAgent();
        RabbitsGrassSimulationAgent agent2 = createAgent();

        space.addAgent(agent1);
        space.addAgent(agent2);

        assertTrue("The space object reported the cell unoccupied",
                   space.isCellOccupied(agent1.getPosition().x, agent2.getPosition().y));

        boolean ret = agent1.tryMove(agent2.getPosition().x, agent2.getPosition().y);
        assertFalse("Agent moved to an occupied cell!", ret);
    }
}
