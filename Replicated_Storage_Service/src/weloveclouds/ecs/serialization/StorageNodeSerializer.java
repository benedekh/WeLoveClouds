package weloveclouds.ecs.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.ecs.serialization.SerializationTokens.*;

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
        String serializedNode = "";

        try {
            
        } catch (Exception e) {

        }
        return serializedNode;
    }
}
