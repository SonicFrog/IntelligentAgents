package ch.epfl.ia;

/**
 * Less cumbersome random generator class
 *
 * @author Ogier
 * @version 1.0
 **/
abstract class RandomUtil {
    /**
     * Generates a random Integer between 0 and upper inclusive
     * @see Integer
     * @see Math.random
     **/
    public static int randomInt(int upper) {
        return (int) (Math.random() * upper);
    }

    /**
     * Generates a random Integer between lower and upper inclusive
     * @see Integer
     * @see Math.random
     **/
    public static int randomInt(int lower, int upper) {
        return randomInt(upper - lower) + lower;
    }
}
