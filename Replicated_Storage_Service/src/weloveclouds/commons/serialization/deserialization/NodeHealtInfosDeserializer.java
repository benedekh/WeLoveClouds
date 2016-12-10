package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;
import static weloveclouds.commons.serialization.models.SerializationConstants.ACTIVE_CONNECTIONS;
import static weloveclouds.commons.serialization.models.SerializationConstants.NAME;
import static weloveclouds.commons.serialization.models.SerializationConstants.SERVER_CONNECTION;

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
        Matcher matcher = getRegexFromToken(NAME).matcher(serializedNodeHealthInfos);
        String deserializedName;

        if (matcher.find()) {
            deserializedName = matcher.group(XML_NODE);
        } else {
            throw new DeserializationException("Unable to deserialize server name from node " +
                    "health infos: " + serializedNodeHealthInfos);
        }
        return deserializedName;
    }

    private ServerConnectionInfo deserializeServerConnectionInfo(String serializedNodeHealthInfos) throws DeserializationException {
        Matcher matcher = getRegexFromToken(SERVER_CONNECTION).matcher
                (serializedNodeHealthInfos);
        ServerConnectionInfo deserializedServerConnectionInfo;

        if (matcher.find()) {
            deserializedServerConnectionInfo = serverConnectionInfoStringDeserializer.deserialize(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from node health infos: " + serializedNodeHealthInfos);
        }
        return deserializedServerConnectionInfo;
    }

    private int deserializeActiveConnectionNumber(String serializedNodeHealthInfos) throws DeserializationException {
        Matcher matcher = getRegexFromToken(ACTIVE_CONNECTIONS).matcher(serializedNodeHealthInfos);
        int deserializedNumberOfActiveConnections;

        if (matcher.find()) {
            deserializedNumberOfActiveConnections = Integer.parseInt(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from node health infos: " + serializedNodeHealthInfos);
        }
        return deserializedNumberOfActiveConnections;
    }
}
