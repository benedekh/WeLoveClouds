package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.SerializationConstants.ACTIVE_CONNECTIONS;
import static weloveclouds.commons.serialization.models.SerializationConstants.NAME;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_HEALTH_INFOS;
import static weloveclouds.commons.serialization.models.SerializationConstants.SERVER_CONNECTION;

/**
 * Created by Benoit on 2016-12-08.
 */
public class NodeHealthInfosSerializer implements ISerializer<AbstractXMLNode, NodeHealthInfos> {
    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer;

    @Inject
    public NodeHealthInfosSerializer(ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer) {
        this.serverConnectionInfoISerializer = serverConnectionInfoISerializer;
    }

    @Override
    public AbstractXMLNode serialize(NodeHealthInfos nodeHealthInfosToSerialize) {
        return new XMLRootNode.Builder()
                .token(NODE_HEALTH_INFOS)
                .addInnerNode(new XMLNode(NAME, nodeHealthInfosToSerialize.getServerName()))
                .addInnerNode(new XMLNode(SERVER_CONNECTION, serverConnectionInfoISerializer.serialize(nodeHealthInfosToSerialize
                        .getServerConnectionInfo())))
                .addInnerNode(new XMLNode(ACTIVE_CONNECTIONS, String.valueOf(nodeHealthInfosToSerialize.getNumberOfActiveConnections())))
                .build();
    }
}
