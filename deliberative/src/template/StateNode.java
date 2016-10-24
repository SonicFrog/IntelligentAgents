package ch.epfl.ia.deliberative;

import java.util.HashSet;
import java.util.Optional;


/**
 * ADT to represent the graph of states for research
 **/
public class StateNode implements Comparable<StateNode> {
    public final AgentState state;
    public final Set<AgentState> children = new HashSet<>();

    private double value;
    private StateNode parent;
    private Color color;

    public enum Color {
        WHITE, GREY, BLACK
    }

    public StateNode(AgentState state) {
        this.state = state;
        this.color = WHITE;
    }

    public void setParent(StateNode parent) {
        this.parent = parent;
    }

    public StateNode getParent() {
        return parent;
    }

    public void addChild(StateNode child) {
        children.add(child);
    }

    public Optional<StateNode> bestChild() {
        return children.stream().sorted().findFirst();
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(StateNode that) {
        return value - that.value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StateNode && ((StateNode) other).state.equals(state);
    }
}
