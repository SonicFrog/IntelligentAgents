package ch.epfl.ia;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

import ch.epfl.ia.RabbitsGrassSimulationSpace;

public class TestSpace {

    public static final int SIZE = 20;

    @Test
    public void testAllCellsEmpty() {
        RabbitsGrassSimulationSpace space = new RabbitsGrassSimulationSpace(SIZE, SIZE);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                assertFalse("No cell should be occupied!", space.isCellOccupied(i, j));
            }
        }
    }
}
