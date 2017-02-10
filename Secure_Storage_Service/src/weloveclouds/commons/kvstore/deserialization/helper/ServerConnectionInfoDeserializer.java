package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.IP_ADDRESS;
import static weloveclouds.commons.serialization.models.XMLTokens.PORT;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link String} to a {@link ServerConnectionInfo}.
 *
 * @author Benedek, Hunton
 */
public class ServerConnectionInfoDeserializer
        implements IDeserializer<ServerConnectionInfo, String> {

    private static final int FAULTY_PORT = -1;

    @Override
    public ServerConnectionInfo deserialize(String from) throws DeserializationException {
        ServerConnectionInfo deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                InetAddress ipAddress = deserializeIpAddress(from);
                int port = deserializePort(from);

                if (ipAddress == null && port == FAULTY_PORT) {
                    return deserialized;
                }

                deserialized =
                        new ServerConnectionInfo.Builder().ipAddress(ipAddress).port(port).build();
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }
        return deserialized;
    }

    private InetAddress deserializeIpAddress(String from) throws DeserializationException {
        Matcher ipAddressFieldMatcher = getRegexFromToken(IP_ADDRESS).matcher(from);
        if (ipAddressFieldMatcher.find()) {
            String ipAddressStr = ipAddressFieldMatcher.group(XML_NODE);
            try {
                return InetAddress.getByName(ipAddressStr);
            } catch (UnknownHostException ex) {
                throw new DeserializationException(StringUtils.join(": ",
                        "Host referred by IP address is unknown", ipAddressStr));
            }
        }
        return null;
    }

    private int deserializePort(String from) throws DeserializationException {
        Matcher portFieldMatcher = getRegexFromToken(PORT).matcher(from);
        if (portFieldMatcher.find()) {
            String portStr = portFieldMatcher.group(XML_NODE);
            try {
                return Integer.valueOf(portStr);
            } catch (NumberFormatException ex) {
                throw new DeserializationException(StringUtils.join("", "Port is NaN: ", portStr));
            }
        }
        return FAULTY_PORT;
    }
}
