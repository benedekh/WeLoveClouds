package weloveclouds.commons.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.CHILD_HASH_RANGES;
import static weloveclouds.commons.serialization.models.XMLTokens.HASH_KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.NODE;
import static weloveclouds.commons.serialization.models.XMLTokens.REPLICAS;

import java.util.List;

import com.google.inject.Inject;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * A serializer which converts a {@link StorageNode} to a {@link AbstractXMLNode}.
 * 
 * @author Benoit
 */
public class StorageNodeSerializer implements ISerializer<AbstractXMLNode, StorageNode> {
    private ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoSerializer;
    private ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer;
    private ISerializer<AbstractXMLNode, NodeHealthInfos> nodeHealthInfosSerializer;
    private ISerializer<String, Hash> hashSerializer;

    @Inject
    public StorageNodeSerializer(
            ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoSerializer,
            ISerializer<String, Hash> hashSerializer,
            ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer,
            ISerializer<AbstractXMLNode, NodeHealthInfos> nodeHealthInfosSerializer) {
        this.serverConnectionInfoSerializer = serverConnectionInfoSerializer;
        this.hashRangeSerializer = hashRangeSerializer;
        this.hashSerializer = hashSerializer;
        this.nodeHealthInfosSerializer = nodeHealthInfosSerializer;
    }

    @Override
    public AbstractXMLNode serialize(StorageNode nodeToSerialize) {
        return new XMLRootNode.Builder().token(NODE)
                .addInnerNode(new XMLNode(NAME, nodeToSerialize.getName()))
                .addInnerNode(serverConnectionInfoSerializer
                        .serialize(nodeToSerialize.getServerConnectionInfo()))
                .addInnerNode(new XMLNode(HASH_KEY,
                        hashSerializer.serialize(nodeToSerialize.getHashKey())))
                .addInnerNode(hashRangeSerializer.serialize(nodeToSerialize.getHashRange()))
                .addInnerNode(nodeHealthInfosSerializer.serialize(nodeToSerialize.getHealthInfos()))
                .addInnerNode(serializeReplicas(nodeToSerialize.getReplicas()))
                .addInnerNode(serializeChildHashRanges(nodeToSerialize.getReadRanges()))
                .build();
    }

    public AbstractXMLNode serializeReplicas(List<StorageNode> replicas) {
        XMLRootNode.Builder replicasXML = new XMLRootNode.Builder().token(REPLICAS);

        for (StorageNode replica : replicas) {
            replicasXML.addInnerNode(
                    serverConnectionInfoSerializer.serialize(replica.getServerConnectionInfo()));
        }
        return replicasXML.build();
    }

    public AbstractXMLNode serializeChildHashRanges(List<HashRange> childHashRanges) {
        XMLRootNode.Builder childHashRangeXML = new XMLRootNode.Builder().token(CHILD_HASH_RANGES);

        for (HashRange childHashRange : childHashRanges) {
            childHashRangeXML.addInnerNode(hashRangeSerializer.serialize(childHashRange));
        }
        return childHashRangeXML.build();
    }
}
