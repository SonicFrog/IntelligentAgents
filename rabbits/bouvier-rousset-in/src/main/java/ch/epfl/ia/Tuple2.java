package ch.epfl.ia;

public class Tuple2<K, V> {
    public final K first;
    public final V second;

    public Tuple2(K first, V second) {
        this.first = first;
        this.second = second;
    }
}
