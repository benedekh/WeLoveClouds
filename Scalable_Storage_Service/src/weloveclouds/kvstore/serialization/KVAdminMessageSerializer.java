package weloveclouds.commons.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;

/**
 * A serializer which converts a {@link KVAdminMessage} to a {@link SerializedMessage}.
 * 
 * @author Benedek
 */
public class KVAdminMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVAdminMessage> {

    public static final String PREFIX = "<KVADMIN>";
    public static final String SEPARATOR = "-ŁŁ-";
    public static final String POSTFIX = "</KVADMIN>";

    private static final Logger LOGGER = Logger.getLogger(KVAdminMessageSerializer.class);

    private ISerializer<String, RingMetadata> metadataSerializer = new RingMetadataSerializer();
    private ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Override
    public SerializedMessage serialize(KVAdminMessage unserializedMessage) {
        LOGGER.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        // original fields
        StatusType status = unserializedMessage.getStatus();
        RingMetadata ringMetadata = unserializedMessage.getRingMetadata();
        RingMetadataPart targetServerInfo = unserializedMessage.getTargetServerInfo();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String ringMetadataStr = metadataSerializer.serialize(ringMetadata);
        String targetServerStr = metadataPartSerializer.serialize(targetServerInfo);
        String responseMessage = unserializedMessage.getResponseMessage();

        // merged string representation
        String serialized = CustomStringJoiner.join(SEPARATOR, statusStr, ringMetadataStr,
                targetServerStr, responseMessage);
        String prefixed = CustomStringJoiner.join("", PREFIX, serialized);
        String postfixed = CustomStringJoiner.join("", prefixed, POSTFIX);

        LOGGER.debug(join(" ", "Serialized message:", postfixed));
        return new SerializedMessage(postfixed);
    }

}
