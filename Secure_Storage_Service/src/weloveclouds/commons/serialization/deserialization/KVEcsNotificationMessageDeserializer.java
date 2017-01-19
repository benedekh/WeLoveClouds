package weloveclouds.commons.serialization.deserialization;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVECS_NOTIFICATION_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.UNRESPONSIVE_NODES_NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.UNRESPONSIVE_NODE_NAME;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.ecs.models.messaging.notification.IKVEcsNotificationMessage;
import weloveclouds.ecs.models.messaging.notification.KVEcsNotificationMessage;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link IKVEcsNotificationMessage},
 *
 * @author Benoit
 */
public class KVEcsNotificationMessageDeserializer
        implements IMessageDeserializer<IKVEcsNotificationMessage, SerializedMessage> {
    private static final Logger LOGGER =
            Logger.getLogger(KVEcsNotificationMessageDeserializer.class);
    private IDeserializer<RingTopology<StorageNode>, String> ringTopologyDeserializer;
    private IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer;

    @Inject
    public KVEcsNotificationMessageDeserializer(
            IDeserializer<RingTopology<StorageNode>, String> ringTopologyDeserializer,
            IDeserializer<NodeHealthInfos, String> nodeHealthInfosDeserializer) {
        this.ringTopologyDeserializer = ringTopologyDeserializer;
        this.nodeHealthInfosDeserializer = nodeHealthInfosDeserializer;
    }

    @Override
    @SuppressWarnings("unchecked")
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
                        .unresponsiveNodesName(deserializeUnresponsiveNodesNameFrom(message))
                        .build());
            } else {
                throw new DeserializationException("Unable to deserialize message: "
                        + new String(serializedMessage.getBytes(), MESSAGE_ENCODING));
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to deserialize ecs notification");
            throw new DeserializationException(e.getMessage());
        }
    }

    @Override
    public IKVEcsNotificationMessage deserialize(byte[] serializedMessage)
            throws DeserializationException {
        return deserialize(new SerializedMessage(new String(serializedMessage, MESSAGE_ENCODING)));
    }

    private IKVEcsNotificationMessage.Status deserializeStatusFrom(String serializedMessage)
            throws DeserializationException {
        Matcher statusMatcher = getRegexFromToken(STATUS).matcher(serializedMessage);
        try {
            if (statusMatcher.find()) {
                return IKVEcsNotificationMessage.Status.valueOf(statusMatcher.group(XML_NODE));
            } else {
                throw new DeserializationException(
                        "Unable to deserialize status from: " + serializedMessage);
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

    private List<String> deserializeUnresponsiveNodesNameFrom(String serializedMessage)
            throws DeserializationException {
        Matcher unresponsiveNodesNameMatcher = getRegexFromToken(UNRESPONSIVE_NODES_NAME)
                .matcher(serializedMessage);
        List<String> unresponsiveNodesName = new ArrayList<>();

        try {
            if (unresponsiveNodesNameMatcher.find()) {
                Matcher unresponsiveNodeNameMatcher =
                        getRegexFromToken(UNRESPONSIVE_NODE_NAME)
                                .matcher(unresponsiveNodesNameMatcher.group(XML_NODE));
                while (unresponsiveNodeNameMatcher.find()) {
                    unresponsiveNodesName.add(unresponsiveNodeNameMatcher.group(XML_NODE));
                }
            }
        } catch (IllegalArgumentException e) {
            throw new DeserializationException("Error while desializing unresponsive nodes name " +
                    "for message: " + serializedMessage);
        }
        return unresponsiveNodesName;
    }

    private IKVEcsNotificationMessage validateDeserializedMessage(
            IKVEcsNotificationMessage deserializedMessage) throws DeserializationException {
        switch (deserializedMessage.getStatus()) {
            case TOPOLOGY_UPDATE:
                if (deserializedMessage.getRingTopology() == null) {
                    throw new DeserializationException("Invalid ecs notification message: " +
                            "Topology update message should contains a ring topology.");
                }
                break;
            case UNRESPONSIVE_NODES_REPORTING:
                if (deserializedMessage.getUnresponsiveNodeNames().isEmpty()) {
                    throw new DeserializationException("Invalid ecs notification message: " +
                            "Unresponsive node reporting message should contains at leat one " +
                            "unresponsive node name");
                }
                break;
        }
        return deserializedMessage;
    }
}
