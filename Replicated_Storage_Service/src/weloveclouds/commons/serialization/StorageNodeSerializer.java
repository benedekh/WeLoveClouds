package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.serialization.models.SerializationConstants;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.serialization.models.SerializedNode;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

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
                            .getHashRange()))
                    .serializedHealthInfos(nodeHealthInfosSerializer.serialize(nodeToSerialize.getHealthInfos()));

            for (StorageNode replica : nodeToSerialize.getReplicas()) {
                serializedNodeBuilder.addSerializedReplica(CustomStringJoiner.join("",
                        SerializationConstants.REPLICA_START_TOKEN, serverConnectionInfoISerializer.serialize(replica
                                .getServerConnectionInfo()), SerializationConstants.REPLICA_END_TOKEN));
            }

            for (HashRange childHashRange : nodeToSerialize.getChildHashranges()) {
                serializedNodeBuilder.addSerializedChildHashRange(CustomStringJoiner.join("",
                        SerializationConstants.CHILD_HASH_RANGE_START_TOKEN, hashRangeSerializer.serialize
                                (childHashRange), SerializationConstants.CHILD_HASH_RANGE_END_TOKEN));
            }
        } catch (Exception e) {
            //log throw
        }
        return serializedNodeBuilder.build().toString();
    }
}
