package ch.epfl.ia;

import java.util.Set;
import java.util.TreeSet;

/**
 * A better wrapper for the actions
 **/
public abstract class SimpleAction {
    Set<SimpleAction> possibleActions = new TreeSet<>();

    public abstract int hashCode();

    public abstract boolean equals(Object that);

    public boolean isMove() {
        return false;
    }

    public boolean isDelivery() {
        return false;
    }

    public static Set<SimpleAction> generateAllActions(List<City> cities) {
        Set<SimpleAction> actions = new TreeSet<>();

        for (City current : cities) {
            for (City dest : cities) {
                actions.add(new )
            }
        }
    }
}
