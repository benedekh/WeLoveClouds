package weloveclouds.commons.loadbalancer.deserialization;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;
import static weloveclouds.commons.serialization.models.XMLTokens.*;

/**
 * Created by Benoit on 2016-12-09.
 */
public class StorageNodeDeserializer implements IDeserializer<StorageNode, String> {
    IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer;
    IDeserializer<HashRange, String> hashRangeDeserializer;
    IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer;

    @Inject
    public StorageNodeDeserializer(IDeserializer<ServerConnectionInfo, String>
                                           serverConnectionInfoDeserializer,
                                   IDeserializer<HashRange, String> hashRangeDeserializer,
                                   IDeserializer<NodeHealthInfos, String>
                                           nodeHealthInfosDeserializer) {
        this.serverConnectionInfoDeserializer = serverConnectionInfoDeserializer;
        this.hashRangeDeserializer = hashRangeDeserializer;
        this.nodeHealthInfosDeserializer = nodeHealthInfosDeserializer;
    }

    @Override
    public StorageNode deserialize(String serializedNode) throws DeserializationException {
        try {
            return new StorageNode.Builder()
                    .name(deserializeNameFrom(serializedNode))
                    .hashRange(deserializeHashRangeFrom(serializedNode))
                    .childHashRanges(deserializeChildHashRangesFrom(serializedNode))
                    .replicas(deserializeReplicasFrom(serializedNode))
                    .healthInfos(deserializeHealthInfosFrom(serializedNode))
                    .serverConnectionInfo(deserializeServerConnectionInfoFrom(serializedNode))
                    .build();
        } catch (Exception e) {
            throw new DeserializationException("Unable to deserialize storage node from: " +
                    serializedNode + " With cause: " + e.getMessage());
        }
    }

    private String deserializeNameFrom(String serializedNode) throws DeserializationException {
        Matcher nameMatcher = getRegexFromToken(NAME).matcher(serializedNode);
        if (nameMatcher.find()) {
            return nameMatcher.group(XML_NODE);
        } else {
            throw new DeserializationException("Unable to deserialize server name " +
                    "from storage node: " + serializedNode);
        }
    }

    private ServerConnectionInfo deserializeServerConnectionInfoFrom(String serializedNode) throws
            DeserializationException {
        Matcher serverConnectionInfoMatcher =
                getRegexFromToken(CONNECTION_INFOS).matcher(serializedNode);
        if (serverConnectionInfoMatcher.find()) {
            return serverConnectionInfoDeserializer
                    .deserialize(serverConnectionInfoMatcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from storage node: " + serializedNode);
        }
    }

    private HashRange deserializeHashRangeFrom(String serializedNode) throws
            DeserializationException {
        Matcher hashRangeMatcher = getRegexFromToken(HASH_RANGE).matcher(serializedNode);
        if (hashRangeMatcher.find()) {
            return hashRangeDeserializer.deserialize(hashRangeMatcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize hash range " +
                    "from storage node: " + serializedNode);
        }
    }

    private NodeHealthInfos deserializeHealthInfosFrom(String serializedNode) throws
            DeserializationException {
        Matcher healthInfosMatcher = getRegexFromToken(NODE_HEALTH_INFOS).matcher(serializedNode);
        if (healthInfosMatcher.find()) {
            return nodeHealthInfosDeserializer.deserialize(healthInfosMatcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize node health infos " +
                    "from storage node: " + serializedNode);
        }
    }

    private List<HashRange> deserializeChildHashRangesFrom(String serializedNode)
            throws DeserializationException {
        Matcher childHashRangesMatcher =
                getRegexFromToken(CHILD_HASH_RANGES).matcher(serializedNode);
        List<HashRange> childHashRangeList = new ArrayList<>();

        if (childHashRangesMatcher.find()) {
            Matcher childHashRangeMatcher = getRegexFromToken(CHILD_HASH_RANGE)
                    .matcher(childHashRangesMatcher.group(XML_NODE));
            while (childHashRangeMatcher.find()) {
                childHashRangeList.add(hashRangeDeserializer
                        .deserialize(childHashRangeMatcher.group(XML_NODE)));
            }
        } else {
            throw new DeserializationException("Unable to deserialize child hash range from : " +
                    serializedNode);
        }
        return childHashRangeList;
    }

    private List<StorageNode> deserializeReplicasFrom(String serializedNode)
            throws DeserializationException {
        Matcher replicasMatcher = getRegexFromToken(REPLICAS).matcher(serializedNode);
        List<StorageNode> replicaList = new ArrayList<>();

        if (replicasMatcher.find()) {
            Matcher replicaMatcher = getRegexFromToken(REPLICA)
                    .matcher(replicasMatcher.group(XML_NODE));
            while (replicaMatcher.find()) {
                String serializedReplica = replicaMatcher.group(XML_NODE);
                replicaList.add(new StorageNode.Builder()
                        .serverConnectionInfo(serverConnectionInfoDeserializer
                                .deserialize(serializedReplica))
                        .build());
            }
        } else {
            throw new DeserializationException("Unable to deserialize replica from : " +
                    serializedNode);
        }
        return replicaList;
    }
}
