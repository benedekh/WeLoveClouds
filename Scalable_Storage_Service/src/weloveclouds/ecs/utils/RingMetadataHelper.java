package weloveclouds.ecs.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import weloveclouds.ecs.models.commands.client.Stop;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.utils.HashingUtil;

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
        HashRange hashRange;
        Hash rangeStart;
        Hash rangeEnd;

        rangeStart = ringPosition == FIRST_NODE_IN_THE_RING ? Hash.MIN_VALUE : previousHashRange.getEnd().incrementByOne();
        rangeEnd = ringPosition == --numberOfNodeInTheRing ? Hash.MAX_VALUE : nodeHashKey;

        return new HashRange.Builder().start(rangeStart).end(rangeEnd).build();
    }

    public static StorageNode getSuccessorFrom(RingTopology<StorageNode> oldTopology,
                                               RingTopology<StorageNode> newTopology, StorageNode newNode) {
        StorageNode successor = null;
        int newNodePosition = newTopology.getRingPositionOf(newNode);

        if (newNodePosition > oldTopology.getLastPosition()) {
            successor = oldTopology.getLastNode();
        } else {
            successor = oldTopology.getNodeAtPosition(newNodePosition);
        }

        return successor;
    }
}
