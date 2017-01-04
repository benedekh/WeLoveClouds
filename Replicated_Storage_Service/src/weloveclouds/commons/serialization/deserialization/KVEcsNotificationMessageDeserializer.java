package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.ecs.models.messaging.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.KVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVECS_NOTIFICATION_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

/**
 * Created by Benoit on 2017-01-04.
 */
public class KVEcsNotificationMessageDeserializer implements
        IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> {
    private IDeserializer<RingTopology<StorageNode>, String> ringTopologyDeserializer;
    private IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer;

    @Inject
    public KVEcsNotificationMessageDeserializer(IDeserializer<RingTopology<StorageNode>, String>
                                                        ringTopologyDeserializer,
                                                IDeserializer<NodeHealthInfos, String>
                                                        nodeHealthInfosDeserializer) {
        this.ringTopologyDeserializer = ringTopologyDeserializer;
        this.nodeHealthInfosDeserializer = nodeHealthInfosDeserializer;
    }

    @Override
    public IKVEcsNotificationMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(KVECS_NOTIFICATION_MESSAGE)
                .matcher(new String(serializedMessage.getBytes(), MESSAGE_ENCODING));

        try {
            if (matcher.find()) {
                String message = matcher.group(XML_NODE);
                return validateDeserializedMessage(new KVEcsNotificationMessage.Builder()
                        .status(deserializeStatusFrom(message))
                        .nodeHealthInfos(deserializeNodeHealthInfosFrom(message))
                        .ringTopology(deserializeRingTopologyFrom(message))
                        .build());
            } else {
                throw new DeserializationException("Unable to deserialize message: " + new String
                        (serializedMessage.getBytes(), MESSAGE_ENCODING));
            }
        } catch (Exception e) {
            throw new DeserializationException(e.getMessage());
        }
    }

    @Override
    public IKVEcsNotificationMessage deserialize(byte[] serializedMessage)
            throws DeserializationException {
        return null;
    }

    private IKVEcsNotificationMessage.Status deserializeStatusFrom(String serializedMessage) throws
            DeserializationException {
        Matcher statusMatcher = getRegexFromToken(STATUS).matcher(serializedMessage);
        try {
            if (statusMatcher.find()) {
                return IKVEcsNotificationMessage.Status.valueOf(statusMatcher.group(XML_NODE));
            } else {
                throw new DeserializationException("Unable to deserialize status from: "
                        + serializedMessage);
            }
        } catch (IllegalArgumentException e) {
            throw new DeserializationException("Invalid status in:" + serializedMessage);
        }
    }

    private NodeHealthInfos deserializeNodeHealthInfosFrom(String serializedMessage) {
        try {
            return nodeHealthInfosDeserializer.deserialize(serializedMessage);
        } catch (DeserializationException e) {
            return null;
        }
    }

    private RingTopology deserializeRingTopologyFrom(String serializedMessage) {
        try {
            return ringTopologyDeserializer.deserialize(serializedMessage);
        } catch (DeserializationException e) {
            return null;
        }
    }

    private IKVEcsNotificationMessage validateDeserializedMessage(IKVEcsNotificationMessage
                                                                          deserializedMessage)
            throws DeserializationException {
        if (deserializedMessage.getRingTopology() == null && deserializedMessage
                .getNodeHealthInfos() == null) {
            throw new DeserializationException("Invalid ecs notification message");
        } else {
            return deserializedMessage;
        }
    }
}
