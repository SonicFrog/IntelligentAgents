package ch.epfl.ia;

import java.util.Random;

/**
 * Less cumbersome random generator class
 *
 * @author Ogier
 * @version 1.0
 **/
abstract class RandomUtil {

    private static Random random = new Random();

    /**
     * Generates a random Integer between 0 and upper inclusive
     * @see Integer
     * @see Math.random
     **/
    public static int randomInt(int upper) {
	assert upper > 0;

	int r = random.nextInt(upper + 1);

	assert 0 <= r;
	assert r <= upper;

	return r;
    }

    /**
     * Generates a random Integer between lower and upper inclusive
     * @see Integer
     * @see Math.random
     **/
    public static int randomInt(int lower, int upper) {
	assert lower < upper;

	int range = Math.abs(lower) + Math.abs(upper) + 1;
        int r = random.nextInt(range) + lower;

	assert lower <= r;
	assert r <= upper;

	return r;
    }
}
