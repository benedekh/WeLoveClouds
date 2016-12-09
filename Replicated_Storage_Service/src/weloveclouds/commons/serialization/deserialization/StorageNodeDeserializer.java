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
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.SerializationConstants.CHILD_HASH_RANGES_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.CHILD_HASH_RANGES_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.CHILD_HASH_RANGE_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.CHILD_HASH_RANGE_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.HASH_RANGE_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.HASH_RANGE_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.NAME_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.NAME_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_END_TOKEN;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_HEALTH_INFOS_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_HEALTH_INFOS_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_START_TOKEN;
import static weloveclouds.commons.serialization.models.SerializationConstants.ORDERED_NODES_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.ORDERED_NODES_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.REPLICAS_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.REPLICAS_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.REPLICA_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.REPLICA_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.SERVER_CONNECTION_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.SERVER_CONNECTION_INFOS_REGEX;

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
                    .childHashranges(null)
                    .replicas(null)
                    .healthInfos(deserializeHealthInfos(serializedNode))
                    .serverConnectionInfo(deserializeServerConnectionInfoFrom(serializedNode))
                    .build();
        } catch (Exception e) {
            //throw log
        }
        return null;
    }

    private String deserializeNameFrom(String serializedNode) throws DeserializationException {
        Matcher matcher = NAME_REGEX.matcher(serializedNode);
        if (matcher.find()) {
            return matcher.group(NAME_GROUP);
        } else {
            throw new DeserializationException("Unable to deserialize server name " +
                    "from storage node: " + serializedNode);
        }
    }

    private ServerConnectionInfo deserializeServerConnectionInfoFrom(String serializedNode) throws
            DeserializationException {
        Matcher matcher = SERVER_CONNECTION_INFOS_REGEX.matcher(serializedNode);
        if (matcher.find()) {
            return serverConnectionInfoDeserializer.deserialize(matcher.group
                    (SERVER_CONNECTION_GROUP));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from storage node: " + serializedNode);
        }
    }

    private HashRange deserializeHashRange(String serializedNode) throws
            DeserializationException {
        Matcher matcher = HASH_RANGE_REGEX.matcher(serializedNode);
        if (matcher.find()) {
            return hashRangeDeserializer.deserialize(matcher.group(HASH_RANGE_GROUP));
        } else {
            throw new DeserializationException("Unable to deserialize hash range " +
                    "from storage node: " + serializedNode);
        }
    }

    private NodeHealthInfos deserializeHealthInfos(String serializedNode) throws
            DeserializationException {
        Matcher matcher = NODE_HEALTH_INFOS_REGEX.matcher(serializedNode);
        if (matcher.find()) {
            return nodeHealthInfosDeserializer.deserialize(matcher.group(NODE_HEALTH_INFOS_GROUP));
        } else {
            throw new DeserializationException("Unable to deserialize node health infos " +
                    "from storage node: " + serializedNode);
        }
    }

    private List<HashRange> deserializeChildHashRanges(String serializedNode) throws DeserializationException {
        Matcher childHashRangesMatcher = CHILD_HASH_RANGES_REGEX.matcher(serializedNode);
        List<HashRange> childHashRangeList = new ArrayList<>();

        if (childHashRangesMatcher.find()) {
            String childHashRanges = childHashRangesMatcher.group(CHILD_HASH_RANGES_GROUP);
            Matcher childHashRangeMatcher = CHILD_HASH_RANGE_REGEX.matcher(childHashRanges);

            while (childHashRangeMatcher.matches()) {
                String serializedChildHashRange = childHashRangeMatcher.group(CHILD_HASH_RANGE_GROUP);
                childHashRangeList.add(hashRangeDeserializer.deserialize(serializedChildHashRange));
                childHashRanges = childHashRanges.replace(serializedChildHashRange, "");
                childHashRangeMatcher = CHILD_HASH_RANGE_REGEX.matcher(childHashRanges);
            }
        } else {
            throw new DeserializationException("Unable to deserialize child hash range from : " +
                    serializedNode);
        }
        return childHashRangeList;
    }

    private List<ServerConnectionInfo> deserializeReplica(String serializedNode) throws DeserializationException{
        Matcher replicasMatcher = REPLICAS_REGEX.matcher(serializedNode);
        List<ServerConnectionInfo> replicaList = new ArrayList<>();

        if (replicasMatcher.find()) {
            String replicas = replicasMatcher.group(REPLICAS_GROUP);
            Matcher replicaMatcher = REPLICA_REGEX.matcher(replicas);

            while (replicaMatcher.matches()) {
                String serializedReplica = replicaMatcher.group(REPLICA_GROUP);
                replicaList.add(serverConnectionInfoDeserializer.deserialize(serializedReplica));
                replicas = replicas.replace(serializedReplica, "");
                replicaMatcher = REPLICA_REGEX.matcher(replicas);
            }
        } else {
            throw new DeserializationException("Unable to deserialize replica from : " +
                    serializedNode);
        }
        return replicaList;
    }
}
