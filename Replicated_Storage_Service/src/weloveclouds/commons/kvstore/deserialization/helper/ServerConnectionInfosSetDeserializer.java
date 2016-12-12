package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFO;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link Set<ServerConnectionInfo>} to a {@link String}.
 * 
 * @author Benedek
 */
public class ServerConnectionInfosSetDeserializer
        implements IDeserializer<Set<ServerConnectionInfo>, String> {

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();

    @Override
    public Set<ServerConnectionInfo> deserialize(String from) throws DeserializationException {
        Set<ServerConnectionInfo> deserialized = null;

        if (from != null && !"null".equals(from)) {
            try {
                deserialized = new HashSet<>();

                Matcher connectionInfosMatcher = getRegexFromToken(CONNECTION_INFO).matcher(from);
                while (connectionInfosMatcher.find()) {
                    deserialized.add(connectionInfoDeserializer
                            .deserialize(connectionInfosMatcher.group(XML_NODE)));
                }

                if (deserialized.isEmpty()) {
                    throw new DeserializationException(CustomStringJoiner.join("",
                            "Unable to extract connection infos from:", from));
                }
            } catch (Exception ex) {
                new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
