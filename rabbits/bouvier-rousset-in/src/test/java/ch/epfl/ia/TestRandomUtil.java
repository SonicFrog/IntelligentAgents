package ch.epfl.ia;

import ch.epfl.ia.RandomUtil;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestRandomUtil {

    @Test
    public void testRandomIntRange() {
	RandomUtil.randomInt(30, 50);
    }

    @Test
    public void testRandomIntRangeWithNegative() {
	RandomUtil.randomInt(-30, 50);
    }

    @Test
    public void testRandomIntRangeZeroRange() {
	assertEquals(RandomUtil.randomInt(30, 30), 30);
    }
}
