package ch.epfl.ia;

import ch.epfl.ia.RabbitsGrassSimulationModel;

import org.junit.Test;
import java.lang.NoSuchMethodException;
import java.lang.reflect.Method;
import static org.junit.Assert.assertEquals;

public class TestModelFrameworkIntegration {

    @Test
    public void testGetInitParam() throws NoSuchMethodException {
        RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
        Class<?> cls = model.getClass();

        for(String methodName : model.getInitParam()) {
            Method meth;

            meth = cls.getMethod("get" + methodName);
            assertEquals(meth.getReturnType(), int.class);

            meth = cls.getMethod("set" + methodName, int.class);
            assertEquals(meth.getReturnType(), Void.TYPE);
        }
    }
}
