package weloveclouds.ecs.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;

/**
 * Created by Benoit on 2016-11-21.
 */
public class RingMetadataHelper {
    private static final int FIRST_NODE_IN_THE_RING = 0;

    public static List<StorageNode> computeRingOrder(List<StorageNode> nodesInTheRing) {
        Collections.sort(nodesInTheRing, new Comparator<StorageNode>() {
            @Override
            public int compare(StorageNode node1, StorageNode node2) {
                return node1.getHashKey().compareTo(node2.getHashKey());
            }
        });
        return nodesInTheRing;
    }

    public static HashRange computeHashRangeForNodeBasedOnRingPosition(int ringPosition, int
            numberOfNodeInTheRing, Hash nodeHashKey, HashRange previousHashRange) {
        Hash rangeStart;
        Hash rangeEnd;

        rangeStart = ringPosition == FIRST_NODE_IN_THE_RING ? Hash.MIN_VALUE : previousHashRange.getEnd().incrementByOne();
        rangeEnd = ringPosition == --numberOfNodeInTheRing ? Hash.MAX_VALUE : nodeHashKey;

        return new HashRange.Builder().begin(rangeStart).end(rangeEnd).build();
    }

    public static StorageNode getSuccessorFrom(RingTopology<StorageNode> oldTopology,
                                               RingTopology<StorageNode> newTopology, StorageNode node) {
        StorageNode successor = null;
        if (newTopology.getNumberOfNodes() > oldTopology.getNumberOfNodes()) {
            int newNodePosition = newTopology.getRingPositionOf(node);

            if (newNodePosition > oldTopology.getLastPosition()) {
                successor = oldTopology.getLastNode();
            } else {
                successor = oldTopology.getNodeAtPosition(newNodePosition);
            }
        } else {
            int oldNodePosition = oldTopology.getRingPositionOf(node);
            if (oldNodePosition == oldTopology.getLastPosition()) {
                successor = oldTopology.getNodeAtPosition(oldNodePosition - 1);
            } else {
                successor = oldTopology.getNodeAtPosition(oldNodePosition + 1);
            }
        }
        return successor;
    }
}
