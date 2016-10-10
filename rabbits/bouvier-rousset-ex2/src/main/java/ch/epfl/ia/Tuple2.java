package ch.epfl.ia;


public class Tuple2<S, T> {
    public final S first;
    public final T second;

    public Tuple2(S first, T second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        if (! (that instanceof Tuple2))
            return false;

        try {
            Tuple2<S, T> val = (Tuple2<S, T>) that;

            return val.first.equals(first) &&
                val.second.equals(second);
        } catch (Exception e) {
            return false;
        }
    }
}
