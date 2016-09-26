package ch.epfl.ia;

/**
 * Data holder for agent positions
 * @author Ogier
 **/
class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
