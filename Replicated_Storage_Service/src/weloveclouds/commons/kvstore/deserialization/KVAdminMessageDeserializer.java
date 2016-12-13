package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVADMIN_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.REMOVABLE_RANGE;
import static weloveclouds.commons.serialization.models.XMLTokens.REPLICAS;
import static weloveclouds.commons.serialization.models.XMLTokens.RESPONSE_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.TARGET_SERVER_INFO;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;
import static weloveclouds.commons.utils.StringUtils.join;

import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfosSetDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public class KVAdminMessageDeserializer
        implements IMessageDeserializer<KVAdminMessage, SerializedMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVAdminMessageDeserializer.class);

    private IDeserializer<HashRange, String> removableRangeDeserializer =
            new HashRangeDeserializer();
    private IDeserializer<RingMetadata, String> metadataDeserializer =
            new RingMetadataDeserializer();
    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();
    private IDeserializer<Set<ServerConnectionInfo>, String> replicaConnectionInfosDeserializer =
            new ServerConnectionInfosSetDeserializer();

    @Override
    public KVAdminMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVAdminMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVAdminMessage from byte[].");
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);

        try {
            Matcher adminMessageMatcher =
                    getRegexFromToken(KVADMIN_MESSAGE).matcher(serializedMessageStr);
            if (adminMessageMatcher.find()) {
                String serializedAdminMessage = adminMessageMatcher.group(XML_NODE);

                if (StringUtils.stringIsNotEmpty(serializedAdminMessage)) {
                    KVAdminMessage deserialized = new KVAdminMessage.Builder()
                            .status(deserializeStatus(serializedAdminMessage))
                            .ringMetadata(deserializeRingMetadata(serializedAdminMessage))
                            .targetServerInfo(deserializeTargetServerInfo(serializedAdminMessage))
                            .replicaConnectionInfos(
                                    deserializeReplicaConnectionInfos(serializedAdminMessage))
                            .removableRange(deserializeRemovableRange(serializedAdminMessage))
                            .responseMessage(deserializeResponseMessage(serializedAdminMessage))
                            .build();
                    LOGGER.debug(
                            join(" ", "Deserialized KVAdminMessage is:", deserialized.toString()));
                    return deserialized;
                } else {
                    throw new DeserializationException("KVAdminMessage is empty.");
                }
            } else {
                throw new DeserializationException(StringUtils.join("",
                        "Unable to extract KVAdminTransferMessage from:", serializedMessageStr));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DeserializationException(ex.getMessage());
        }
    }

    private StatusType deserializeStatus(String from) throws DeserializationException {
        Matcher statusMatcher = getRegexFromToken(STATUS).matcher(from);
        if (statusMatcher.find()) {
            String statusStr = statusMatcher.group(XML_NODE);
            try {
                return StatusType.valueOf(statusStr);
            } catch (IllegalArgumentException ex) {
                throw new DeserializationException("StatusType is not recognized.");
            }
        } else {
            throw new DeserializationException(
                    StringUtils.join("", "Unable to extract status from:", from));
        }
    }

    private RingMetadata deserializeRingMetadata(String from) throws DeserializationException {
        Matcher ringMetadataMatcher = getRegexFromToken(RING_METADATA).matcher(from);
        if (ringMetadataMatcher.find()) {
            return metadataDeserializer.deserialize(ringMetadataMatcher.group(XML_NODE));
        } else {
            return null;
        }
    }

    private RingMetadataPart deserializeTargetServerInfo(String from)
            throws DeserializationException {
        Matcher metadataPartMatcher = getRegexFromToken(TARGET_SERVER_INFO).matcher(from);
        if (metadataPartMatcher.find()) {
            return metadataPartDeserializer.deserialize(metadataPartMatcher.group(XML_NODE));
        } else {
            return null;
        }
    }

    private Set<ServerConnectionInfo> deserializeReplicaConnectionInfos(String from)
            throws DeserializationException {
        Matcher replicaConnectionInfosMatcher = getRegexFromToken(REPLICAS).matcher(from);
        if (replicaConnectionInfosMatcher.find()) {
            return replicaConnectionInfosDeserializer
                    .deserialize(replicaConnectionInfosMatcher.group(XML_NODE));
        } else {
            return null;
        }
    }

    private HashRange deserializeRemovableRange(String from) throws DeserializationException {
        Matcher removableRangeMatcher = getRegexFromToken(REMOVABLE_RANGE).matcher(from);
        if (removableRangeMatcher.find()) {
            return removableRangeDeserializer.deserialize(removableRangeMatcher.group(XML_NODE));
        } else {
            return null;
        }
    }

    private String deserializeResponseMessage(String from) throws DeserializationException {
        Matcher responseMessageMatcher = getRegexFromToken(RESPONSE_MESSAGE).matcher(from);
        if (responseMessageMatcher.find()) {
            String deserialized = responseMessageMatcher.group(XML_NODE);
            if (StringUtils.stringIsNotEmpty(deserialized)) {
                return deserialized;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
