package weloveclouds.ecs.models.topology;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weloveclouds.ecs.models.repository.AbstractNode;

/**
 * Created by Benoit on 2016-11-23.
 */
public class RingTopology<T extends AbstractNode> {
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
        return new ArrayList<>(this.nodes);
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

    public T getNextNodeFrom(T node) {
        int nodePosition = nodes.indexOf(node);
        T nextNode = null;

        if (nodePosition == getLastPosition()) {
            nextNode = getFirstNode();
        } else {
            nextNode = nodes.get(++nodePosition);
        }

        return nextNode;
    }

    public List<T> getReplicasOf(T node, int numberOfNeighbours) {
        List<T> neighbours = new ArrayList<T>();
        T neighbour = node;

        for (int i = 0; i < numberOfNeighbours; i++) {
            neighbour = getNextNodeFrom(neighbour);

            if (neighbour != node) {
                neighbours.add(neighbour);
            } else {
                i = numberOfNeighbours;
            }
        }
        return neighbours;
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
