package ch.epfl.ia.deliberative;

public class AgentState {
    private final double availableSpace;
    private final City currentCity;
    private final HashMap<T, V> tasks;

    public AgentState(double availableSpace, City currentCity, HashMap<Integer, TaskDetail> tasks) {
        this.availableSpace = availableSpace;
        this.currentCity = currentCity;
        this.tasks = tasks;
    }

    @Override
    public int hashCode() {
        // TODO: stub
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        return true;
    }

    public AgentState apply(Action a) {
        return new a.accept(new ActionApplier());
    }

    /**
     * Visitor to apply an action to a given state for a transition
     **/
    public class ActionApplier implements ActionHandler<AgentState> {
        public AgentState moveTo(City c) {
            if (currentCity.equals(c) || !(currentCity.hasNeighbor(c))) {
                return null;
            }

            return new AgentState(availableSpace, c, tasks);
        }

        public AgentState pickup(Task t) {
            return null;
        }

        public AgentState deliver(Task t) {
            return null;
        }
    }
}
