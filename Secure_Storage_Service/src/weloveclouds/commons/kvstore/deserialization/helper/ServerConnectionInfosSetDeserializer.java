package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFO;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link String} to a {@link Set<ServerConnectionInfo>}.
 * 
 * @author Benedek, Hunton
 */
public class ServerConnectionInfosSetDeserializer
        implements IDeserializer<Set<ServerConnectionInfo>, String> {

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();

    @Override
    public Set<ServerConnectionInfo> deserialize(String from) throws DeserializationException {
        Set<ServerConnectionInfo> deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                deserialized = new HashSet<>();
                Matcher connectionInfosMatcher = getRegexFromToken(CONNECTION_INFO).matcher(from);
                while (connectionInfosMatcher.find()) {
                    deserialized.add(connectionInfoDeserializer
                            .deserialize(connectionInfosMatcher.group(XML_NODE)));
                }
                if (deserialized.isEmpty()) {
                    return null;
                }
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
