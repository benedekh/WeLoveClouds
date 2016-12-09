package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.serialization.models.SerializationConstants;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-08.
 */
public class NodeHealthInfosSerializer implements ISerializer<String, NodeHealthInfos> {
    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer;

    @Inject
    public NodeHealthInfosSerializer(ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer) {
        this.serverConnectionInfoISerializer = serverConnectionInfoISerializer;
    }

    @Override
    public String serialize(NodeHealthInfos nodeHealthInfosToSerialize) {
        return CustomStringJoiner.join("",
                SerializationConstants.NODE_HEALTH_INFOS_START_TOKEN,
                SerializationConstants.NAME_START_TOKEN,
                nodeHealthInfosToSerialize.getServerName(),
                SerializationConstants.NAME_END_TOKEN,
                SerializationConstants.SERVER_CONNECTION_START_TOKEN,
                serverConnectionInfoISerializer.serialize(nodeHealthInfosToSerialize
                        .getServerConnectionInfo()),
                SerializationConstants.SERVER_CONNECTION_END_TOKEN,
                SerializationConstants.ACTIVE_CONNECTIONS_START_TOKEN,
                String.valueOf(nodeHealthInfosToSerialize.getNumberOfActiveConnections()),
                SerializationConstants.ACTIVE_CONNECTIONS_END_TOKEN,
                SerializationConstants.NODE_HEALTH_INFOS_END_TOKEN);
    }
}
