package weloveclouds.ecs.models.topology;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benoit on 2016-11-23.
 */
public class RingTopology<T> {
    private static final int FIRST_NODE_INDEX = 0;
    List<T> nodes;

    public RingTopology() {
        nodes = new ArrayList<T>();
    }

    public RingTopology(List<T> nodes) {
        this.nodes = new ArrayList<T>(nodes);
    }

    public RingTopology(RingTopology<T> ringTopology) {
        this.nodes = new ArrayList<>(ringTopology.getNodes());
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }

    public List<T> getNodes() {
        return new ArrayList<T>(this.nodes);
    }

    public int getLastPosition() {
        return this.nodes.size() - 1;
    }

    public T getNodeAtPosition(int position) {
        return this.nodes.get(position);
    }

    public T getFirstNode() {
        return nodes.get(FIRST_NODE_INDEX);
    }

    public T getLastNode() {
        return nodes.get(nodes.size() - 1);
    }

    public int getRingPositionOf(T node) {
        return nodes.indexOf(node);
    }

    public RingTopology removeNodes(T node) {
        this.nodes.remove(node);
        return this;
    }

    public RingTopology<T> updateWith(List<T> nodes) {
        this.nodes = nodes;
        return this;
    }

    public RingTopology<T> updateWith(RingTopology newRingTopology) {
        return updateWith(newRingTopology.getNodes());
    }
}
