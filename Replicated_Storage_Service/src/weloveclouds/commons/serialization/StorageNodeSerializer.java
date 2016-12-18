package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import java.util.List;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

import static weloveclouds.commons.serialization.models.XMLTokens.CHILD_HASH_RANGES;
import static weloveclouds.commons.serialization.models.XMLTokens.CHILD_HASH_RANGE;
import static weloveclouds.commons.serialization.models.XMLTokens.HASH_KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.HASH_RANGE;
import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.NODE;
import static weloveclouds.commons.serialization.models.XMLTokens.REPLICAS;
import static weloveclouds.commons.serialization.models.XMLTokens.REPLICA;
import static weloveclouds.commons.serialization.models.XMLTokens.SERVER_CONNECTION;

/**
 * Created by Benoit on 2016-12-08.
 */
public class StorageNodeSerializer implements ISerializer<AbstractXMLNode, StorageNode> {
    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer;
    private ISerializer<String, Hash> hashSerializer;
    private ISerializer<String, HashRange> hashRangeSerializer;
    private ISerializer<AbstractXMLNode, ServiceHealthInfos> nodeHealthInfosSerializer;

    @Inject
    public StorageNodeSerializer(ISerializer<String, ServerConnectionInfo>
                                         serverConnectionInfoISerializer,
                                 ISerializer<String, Hash> hashSerializer,
                                 ISerializer<String, HashRange> hashRangeSerializer,
                                 ISerializer<AbstractXMLNode, ServiceHealthInfos>
                                         nodeHealthInfosSerializer) {
        this.serverConnectionInfoISerializer = serverConnectionInfoISerializer;
        this.hashRangeSerializer = hashRangeSerializer;
        this.hashSerializer = hashSerializer;
        this.nodeHealthInfosSerializer = nodeHealthInfosSerializer;
    }

    @Override
    public AbstractXMLNode serialize(StorageNode nodeToSerialize) {
        return new XMLRootNode.Builder().token(NODE)
                .addInnerNode(new XMLNode(NAME, nodeToSerialize.getName()))
                .addInnerNode(new XMLNode(SERVER_CONNECTION, serverConnectionInfoISerializer
                        .serialize(nodeToSerialize.getServerConnectionInfo())))
                .addInnerNode(new XMLNode(HASH_KEY, hashSerializer
                        .serialize(nodeToSerialize.getHashKey())))
                .addInnerNode(new XMLNode(HASH_RANGE, hashRangeSerializer
                        .serialize(nodeToSerialize.getHashRange())))
                .addInnerNode(nodeHealthInfosSerializer.serialize(nodeToSerialize.getHealthInfos()))
                .addInnerNode(serializeReplicas(nodeToSerialize.getReplicas()))
                .addInnerNode(serializeChildHashRanges(nodeToSerialize.getChildHashRanges()))
                .build();
    }

    public AbstractXMLNode serializeReplicas(List<StorageNode> replicas) {
        XMLRootNode.Builder replicasXML = new XMLRootNode.Builder().token(REPLICAS);

        for (StorageNode replica : replicas) {
            replicasXML.addInnerNode(new XMLNode(REPLICA, serverConnectionInfoISerializer
                    .serialize(replica.getServerConnectionInfo())));
        }
        return replicasXML.build();
    }

    public AbstractXMLNode serializeChildHashRanges(List<HashRange> childHashRanges) {
        XMLRootNode.Builder childHashRangeXML = new XMLRootNode.Builder().token(CHILD_HASH_RANGES);

        for (HashRange childHashRange : childHashRanges) {
            childHashRangeXML.addInnerNode(new XMLNode(CHILD_HASH_RANGE, hashRangeSerializer
                    .serialize(childHashRange)));
        }
        return childHashRangeXML.build();
    }
}
