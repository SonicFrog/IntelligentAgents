package ch.epfl.ia;

import logist.topology.Topology.City;

/**
 * A better move wrapper
 **/
public class SimpleMove extends SimpleAction {
    public final City from;
    public final City to;

    public SimpleMove(City from, City to) {
        super();
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean isMove() {
        return true;
    }

    @Override
    public boolean isDelivery() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;

        hash = prime * hash + ((from != null) ? from.hashCode() : 0);
        hash = prime * hash + ((to != null) ? to.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object other) {
        return false;
    }
}
