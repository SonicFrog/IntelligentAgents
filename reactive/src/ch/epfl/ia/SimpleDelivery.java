package ch.epfl.ia;

public class SimpleDelivery extends SimpleAction {
    public SimpleDelivery() {
        super();
    }

    @Override
    public boolean isMove() {
        return false;
    }

    @Override
    public boolean isDelivery() {
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof SimpleDelivery;
    }

    @Override
    public String toString() {
        return "Delivery action";
    }
}
