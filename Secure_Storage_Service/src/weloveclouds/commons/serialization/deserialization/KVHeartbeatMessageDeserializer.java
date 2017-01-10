package weloveclouds.commons.serialization.deserialization;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.NODE_HEALTH_INFOS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.regex.Matcher;

import com.google.inject.Inject;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.models.IKVHeartbeatMessage;
import weloveclouds.loadbalancer.models.KVHeartbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link IKVHeartbeatMessage}.
 * 
 * @author Benoit
 */
public class KVHeartbeatMessageDeserializer
        implements IMessageDeserializer<IKVHeartbeatMessage, SerializedMessage> {
    private IDeserializer<NodeHealthInfos, String> healthInfosDeserializer;

    @Inject
    public KVHeartbeatMessageDeserializer(
            IDeserializer<NodeHealthInfos, String> healthInfosDeserializer) {
        this.healthInfosDeserializer = healthInfosDeserializer;
    }

    @Override
    public IKVHeartbeatMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(NODE_HEALTH_INFOS)
                .matcher(new String(serializedMessage.getBytes(), MESSAGE_ENCODING));

        try {
            if (matcher.find()) {
                return new KVHeartbeatMessage(
                        healthInfosDeserializer.deserialize(matcher.group(XML_NODE)));
            } else {
                throw new DeserializationException("Unable to deserialize message: "
                        + new String(serializedMessage.getBytes(), MESSAGE_ENCODING));
            }
        } catch (Exception e) {
            throw new DeserializationException(e.getMessage());
        }
    }

    @Override
    public IKVHeartbeatMessage deserialize(byte[] serializedMessage)
            throws DeserializationException {
        return deserialize(new SerializedMessage(new String(serializedMessage, MESSAGE_ENCODING)));
    }
}
