package weloveclouds.kvstore.deserialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import static weloveclouds.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.kvstore.deserialization.helper.RingMetadataDeserializer;
import weloveclouds.kvstore.deserialization.helper.RingMetadataPartDeserializer;
import weloveclouds.kvstore.deserialization.helper.SetOfServerConnectionInfosDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVAdminMessage}.
 * 
 * @author Benedek
 */
public class KVAdminMessageDeserializer
        implements IMessageDeserializer<KVAdminMessage, SerializedMessage> {

    private static final int NUMBER_OF_MESSAGE_PARTS = 6;
    private static final int MESSAGE_STATUS_INDEX = 0;
    private static final int MESSAGE_RING_METADATA_INDEX = 1;
    private static final int MESSAGE_TARGET_SERVER_INFO_INDEX = 2;
    private static final int MESSAGE_REPLICA_CONNECTION_INFOS_INDEX = 3;
    private static final int MESSAGE_REMOVABLE_RANGE_INDEX = 4;
    private static final int MESSAGE_RESPONSE_MESSAGE_INDEX = 5;

    private static final Logger LOGGER = Logger.getLogger(KVAdminMessageDeserializer.class);

    private IDeserializer<HashRange, String> removableRangeDeserializer =
            new HashRangeDeserializer();
    private IDeserializer<RingMetadata, String> metadataDeserializer =
            new RingMetadataDeserializer();
    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();
    private IDeserializer<Set<ServerConnectionInfo>, String> replicaConnectionInfosDeserializer =
            new SetOfServerConnectionInfosDeserializer();

    @Override
    public KVAdminMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVAdminMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVAdminMessage from byte[].");

        // remove prefix and postfix
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        serializedMessageStr = serializedMessageStr.replace(KVAdminMessageSerializer.PREFIX, "");
        serializedMessageStr = serializedMessageStr.replace(KVAdminMessageSerializer.POSTFIX, "");

        // raw message split
        String[] messageParts = serializedMessageStr.split(KVAdminMessageSerializer.SEPARATOR);

        // length check
        if (messageParts.length != NUMBER_OF_MESSAGE_PARTS) {
            throw new DeserializationException(
                    CustomStringJoiner.join("", "Message must consist of exactly ",
                            String.valueOf(NUMBER_OF_MESSAGE_PARTS), " parts."));
        }

        try {
            // raw fields
            String statusStr = messageParts[MESSAGE_STATUS_INDEX];
            String ringMetadataStr = messageParts[MESSAGE_RING_METADATA_INDEX];
            String targetServerInfoStr = messageParts[MESSAGE_TARGET_SERVER_INFO_INDEX];
            String replicaConnectionInfosStr = messageParts[MESSAGE_REPLICA_CONNECTION_INFOS_INDEX];
            String removableRangeStr = messageParts[MESSAGE_REMOVABLE_RANGE_INDEX];
            String responseMessageStr = messageParts[MESSAGE_RESPONSE_MESSAGE_INDEX];

            // deserialized fields
            StatusType status = StatusType.valueOf(statusStr);
            RingMetadata ringMetadata = metadataDeserializer.deserialize(ringMetadataStr);
            RingMetadataPart targetServerInfo =
                    metadataPartDeserializer.deserialize(targetServerInfoStr);
            HashRange removableRange = removableRangeDeserializer.deserialize(removableRangeStr);
            Set<ServerConnectionInfo> replicaConnectionInfos =
                    replicaConnectionInfosDeserializer.deserialize(replicaConnectionInfosStr);
            String responseMessage = "null".equals(responseMessageStr) ? null : responseMessageStr;

            // deserialized object
            KVAdminMessage deserialized = new KVAdminMessage.Builder().status(status)
                    .ringMetadata(ringMetadata).targetServerInfo(targetServerInfo)
                    .replicaConnectionInfos(replicaConnectionInfos).removableRange(removableRange)
                    .responseMessage(responseMessage).build();

            LOGGER.debug(join(" ", "Deserialized KVAdminMessage is:", deserialized.toString()));
            return deserialized;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex);
            throw new DeserializationException("StatusType is not recognized.");
        }
    }
}
