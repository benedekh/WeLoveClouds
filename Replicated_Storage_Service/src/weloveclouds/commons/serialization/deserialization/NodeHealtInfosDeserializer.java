package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;
import static weloveclouds.commons.serialization.models.XMLTokens.ACTIVE_CONNECTIONS;
import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFOS;

/**
 * Created by Benoit on 2016-12-09.
 */
public class NodeHealtInfosDeserializer implements IDeserializer<NodeHealthInfos, String> {
    IDeserializer<ServerConnectionInfo, String> serverConnectionInfoStringDeserializer;

    @Inject
    public NodeHealtInfosDeserializer(IDeserializer<ServerConnectionInfo, String>
                                              serverConnectionInfoStringDeserializer) {
        this.serverConnectionInfoStringDeserializer = serverConnectionInfoStringDeserializer;
    }

    @Override
    public NodeHealthInfos deserialize(String serializedNodeHealthInfos)
            throws DeserializationException {
        try {
            return new NodeHealthInfos.Builder()
                    .serverName(deserializeNameFrom(serializedNodeHealthInfos))
                    .serverConnectionInfo(
                            deserializeServerConnectionInfoFrom(serializedNodeHealthInfos))
                    .numberOfActiveConnections(
                            deserializeActiveConnectionNumberFrom(serializedNodeHealthInfos))
                    .build();
        } catch (Exception e) {
            throw new DeserializationException("Unable to deserialize node health info: " +
                    serializedNodeHealthInfos);
        }
    }

    private String deserializeNameFrom(String serializedNodeHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(NAME).matcher(serializedNodeHealthInfos);

        if (matcher.find()) {
            return matcher.group(XML_NODE);
        } else {
            throw new DeserializationException("Unable to deserialize server name from node " +
                    "health infos: " + serializedNodeHealthInfos);
        }
    }

    private ServerConnectionInfo deserializeServerConnectionInfoFrom(String serializedNodeHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(CONNECTION_INFOS).matcher(serializedNodeHealthInfos);

        if (matcher.find()) {
            return serverConnectionInfoStringDeserializer.deserialize(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from node health infos: " + serializedNodeHealthInfos);
        }
    }

    private int deserializeActiveConnectionNumberFrom(String serializedNodeHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(ACTIVE_CONNECTIONS).matcher(serializedNodeHealthInfos);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize server connection info " +
                    "from node health infos: " + serializedNodeHealthInfos);
        }
    }
}
