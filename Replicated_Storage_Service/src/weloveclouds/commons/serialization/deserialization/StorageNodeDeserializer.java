package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;
import static weloveclouds.commons.serialization.models.SerializationConstants.*;

/**
 * Created by Benoit on 2016-12-09.
 */
public class StorageNodeDeserializer implements IDeserializer<StorageNode, String> {
    IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer;
    IDeserializer<HashRange, String> hashRangeDeserializer;
    IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer;

    @Inject
    public StorageNodeDeserializer(IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer,
                                   IDeserializer<HashRange, String> hashRangeDeserializer,
                                   IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer) {
        this.serverConnectionInfoDeserializer = serverConnectionInfoDeserializer;
        this.hashRangeDeserializer = hashRangeDeserializer;
        this.nodeHealthInfosDeserializer = nodeHealthInfosDeserializer;
    }

    @Override
    public StorageNode deserialize(String serializedNode) throws DeserializationException {
        StorageNode storageNode;
        try {
            storageNode = new StorageNode.Builder()
                    .id(deserializeNameFrom(serializedNode))
                    .hashRange(deserializeHashRange(serializedNode))
                    .childHashranges(deserializeChildHashRanges(serializedNode))
                    .replicas(deserializeReplica(serializedNode))
                    .healthInfos(deserializeHealthInfos(serializedNode))
                    .serverConnectionInfo(deserializeServerConnectionInfoFrom(serializedNode))
                    .build();
        } catch (Exception e) {
            throw new DeserializationException("Unable to deserialize storage node from: " +
                    serializedNode + " With cause: " + e.getMessage());
        }
        return storageNode;
    }

    private String deserializeNameFrom(String serializedNode) throws DeserializationException {
        Matcher matcher = getRegexFromToken(NAME).matcher(serializedNode);
        if (matcher.find()) {
            return matcher.group(XML_NODE);
        } else {
            throw new DeserializationException("Unable to deserialize server name " +
                    "from storage node: " + serializedNode);
        }
    }

    private ServerConnectionInfo deserializeServerConnectionInfoFrom(String serializedNode) throws
            DeserializationException {
        Matcher matcher = getRegexFromToken(SERVER_CONNECTION).matcher(serializedNode);
        if (matcher.find()) {
            return serverConnectionInfoDeserializer.deserialize(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from storage node: " + serializedNode);
        }
    }

    private HashRange deserializeHashRange(String serializedNode) throws
            DeserializationException {
        Matcher matcher = getRegexFromToken(HASH_RANGE).matcher(serializedNode);
        if (matcher.find()) {
            return hashRangeDeserializer.deserialize(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize hash range " +
                    "from storage node: " + serializedNode);
        }
    }

    private NodeHealthInfos deserializeHealthInfos(String serializedNode) throws
            DeserializationException {
        Matcher matcher = getRegexFromToken(NODE_HEALTH_INFOS).matcher(serializedNode);
        if (matcher.find()) {
            return nodeHealthInfosDeserializer.deserialize(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize node health infos " +
                    "from storage node: " + serializedNode);
        }
    }

    private List<HashRange> deserializeChildHashRanges(String serializedNode) throws DeserializationException {
        Matcher childHashRangesMatcher = getRegexFromToken(CHILD_HASH_RANGES).matcher(serializedNode);
        List<HashRange> childHashRangeList = new ArrayList<>();

        if (childHashRangesMatcher.find()) {
            Matcher childHashRangeMatcher = getRegexFromToken(CHILD_HASH_RANGE).matcher
                    (childHashRangesMatcher.group(XML_NODE));
            while (childHashRangeMatcher.find()) {
                childHashRangeList.add(hashRangeDeserializer.deserialize(childHashRangeMatcher.group(XML_NODE)));
            }
        } else {
            throw new DeserializationException("Unable to deserialize child hash range from : " +
                    serializedNode);
        }
        return childHashRangeList;
    }

    private List<StorageNode> deserializeReplica(String serializedNode) throws DeserializationException {
        Matcher replicasMatcher = getRegexFromToken(REPLICAS).matcher(serializedNode);
        List<StorageNode> replicaList = new ArrayList<>();

        if (replicasMatcher.find()) {
            Matcher replicaMatcher = getRegexFromToken(REPLICA).matcher(replicasMatcher.group(XML_NODE));
            while (replicaMatcher.find()) {
                String serializedReplica = replicaMatcher.group(XML_NODE);
                replicaList.add(new StorageNode.Builder()
                        .serverConnectionInfo(serverConnectionInfoDeserializer.deserialize(serializedReplica))
                        .build());
            }
        } else {
            throw new DeserializationException("Unable to deserialize replica from : " +
                    serializedNode);
        }
        return replicaList;
    }
}
