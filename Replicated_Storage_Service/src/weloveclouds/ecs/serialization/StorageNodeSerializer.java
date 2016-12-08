package weloveclouds.ecs.serialization;

import com.google.inject.Inject;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.serialization.models.SerializedNode;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.ecs.serialization.SerializationTokens.CHILD_HASH_RANGE_END_TOKEN;
import static weloveclouds.ecs.serialization.SerializationTokens.CHILD_HASH_RANGE_START_TOKEN;
import static weloveclouds.ecs.serialization.SerializationTokens.REPLICA_END_TOKEN;
import static weloveclouds.ecs.serialization.SerializationTokens.REPLICA_START_TOKEN;

/**
 * Created by Benoit on 2016-12-08.
 */
public class StorageNodeSerializer implements ISerializer<String, StorageNode> {
    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer;
    private ISerializer<String, Hash> hashSerializer;
    private ISerializer<String, HashRange> hashRangeSerializer;
    private ISerializer<String, NodeHealthInfos> nodeHealthInfosSerializer;

    @Inject
    public StorageNodeSerializer(ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer,
                                 ISerializer<String, Hash> hashSerializer,
                                 ISerializer<String, HashRange> hashRangeSerializer,
                                 ISerializer<String, NodeHealthInfos> nodeHealthInfosSerializer) {
        this.serverConnectionInfoISerializer = serverConnectionInfoISerializer;
        this.hashRangeSerializer = hashRangeSerializer;
        this.hashSerializer = hashSerializer;
        this.nodeHealthInfosSerializer = nodeHealthInfosSerializer;
    }

    @Override
    public String serialize(StorageNode nodeToSerialize) {
        SerializedNode.Builder serializedNodeBuilder = new SerializedNode.Builder();

        try {
            serializedNodeBuilder
                    .serializedName(nodeToSerialize.getId())
                    .serializedConnectionInfos(serverConnectionInfoISerializer.serialize
                            (nodeToSerialize.getServerConnectionInfo()))
                    .serializedHashKey(hashSerializer.serialize(nodeToSerialize.getHashKey()))
                    .serializedHashRange(hashRangeSerializer.serialize(nodeToSerialize
                            .getHashRange()));

            for (StorageNode replica : nodeToSerialize.getReplicas()) {
                serializedNodeBuilder.addSerializedReplica(CustomStringJoiner.join("",
                        REPLICA_START_TOKEN, serverConnectionInfoISerializer.serialize(replica
                                .getServerConnectionInfo()), REPLICA_END_TOKEN));
            }

            for (HashRange childHashRange : nodeToSerialize.getChildHashranges()) {
                serializedNodeBuilder.addSerializedChildHashRange(CustomStringJoiner.join("",
                        CHILD_HASH_RANGE_START_TOKEN, hashRangeSerializer.serialize
                                (childHashRange), CHILD_HASH_RANGE_END_TOKEN));
            }
        } catch (Exception e) {
            //log throw
        }
        return serializedNodeBuilder.build().toString();
    }
}
