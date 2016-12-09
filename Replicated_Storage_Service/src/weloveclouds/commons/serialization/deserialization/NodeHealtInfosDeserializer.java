package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.SerializationConstants.ACTIVE_CONNECTIONS_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.ACTIVE_NUMBER_OF_CONNECTIONS_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.NAME_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.NAME_REGEX;
import static weloveclouds.commons.serialization.models.SerializationConstants.SERVER_CONNECTION_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.SERVER_CONNECTION_INFOS_REGEX;

/**
 * Created by Benoit on 2016-12-09.
 */
public class NodeHealtInfosDeserializer implements IDeserializer<NodeHealthInfos, String> {
    IDeserializer<ServerConnectionInfo, String> serverConnectionInfoStringDeserializer;

    @Inject
    public NodeHealtInfosDeserializer(IDeserializer<ServerConnectionInfo, String> serverConnectionInfoStringDeserializer) {
        this.serverConnectionInfoStringDeserializer = serverConnectionInfoStringDeserializer;
    }

    @Override
    public NodeHealthInfos deserialize(String serializedNodeHealthInfos) throws DeserializationException {
        NodeHealthInfos nodeHealthInfos;

        try {
            nodeHealthInfos = new NodeHealthInfos.Builder()
                    .serverName(deserializeName(serializedNodeHealthInfos))
                    .serverConnectionInfo(deserializeServerConnectionInfo(serializedNodeHealthInfos))
                    .numberOfActiveConnections(deserializeActiveConnectionNumber(serializedNodeHealthInfos))
                    .build();
        } catch (Exception e) {
            throw new DeserializationException("Unable to deserialize node health info: " +
                    serializedNodeHealthInfos);
        }

        return nodeHealthInfos;
    }

    private String deserializeName(String serializedNodeHealthInfos) throws DeserializationException {
        Matcher matcher = NAME_REGEX.matcher(serializedNodeHealthInfos);
        String deserializedName;

        if (matcher.find()) {
            deserializedName = matcher.group(NAME_GROUP);
        } else {
            throw new DeserializationException("Unable to deserialize server name from node " +
                    "health infos: " + serializedNodeHealthInfos);
        }
        return deserializedName;
    }

    private ServerConnectionInfo deserializeServerConnectionInfo(String serializedNodeHealthInfos) throws DeserializationException {
        Matcher matcher = SERVER_CONNECTION_INFOS_REGEX.matcher(serializedNodeHealthInfos);
        ServerConnectionInfo deserializedServerConnectionInfo;

        if (matcher.find()) {
            deserializedServerConnectionInfo = serverConnectionInfoStringDeserializer.deserialize
                    (matcher.group(SERVER_CONNECTION_GROUP));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from node health infos: " + serializedNodeHealthInfos);
        }
        return deserializedServerConnectionInfo;
    }

    private int deserializeActiveConnectionNumber(String serializedNodeHealthInfos) throws DeserializationException {
        Matcher matcher = ACTIVE_NUMBER_OF_CONNECTIONS_REGEX.matcher(serializedNodeHealthInfos);
        int deserializedNumberOfActiveConnections;

        if (matcher.find()) {
            deserializedNumberOfActiveConnections = Integer.parseInt(matcher.group
                    (ACTIVE_CONNECTIONS_GROUP));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from node health infos: " + serializedNodeHealthInfos);
        }
        return deserializedNumberOfActiveConnections;
    }
}
