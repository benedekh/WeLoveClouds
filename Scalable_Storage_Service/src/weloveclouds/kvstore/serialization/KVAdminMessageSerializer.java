package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.kvstore.serialization.helper.ServerInitializationContextSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.ServerInitializationContext;

/**
 * An exact serializer which converts a {@link KVAdminMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVAdminMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVAdminMessage> {

    public static final String SEPARATOR = "-\r-";

    private ISerializer<String, ServerInitializationContext> initializationContextSerializer =
            new ServerInitializationContextSerializer();
    private ISerializer<String, RingMetadata> metadataSerializer = new RingMetadataSerializer();
    private ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public SerializedMessage serialize(KVAdminMessage unserializedMessage) {
        logger.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        // original fields
        StatusType status = unserializedMessage.getStatus();
        ServerInitializationContext initializationContext =
                unserializedMessage.getInitializationContext();
        RingMetadata ringMetadata = unserializedMessage.getRingMetadata();
        RingMetadataPart targetServerInfo = unserializedMessage.getTargetServerInfo();

        // string representation
        String statusStr = status == null ? null : status.toString();

        String initializationContextStr =
                initializationContextSerializer.serialize(initializationContext);
        String ringMetadataStr = metadataSerializer.serialize(ringMetadata);
        String targetServerStr = metadataPartSerializer.serialize(targetServerInfo);
        String responseMessage = unserializedMessage.getResponseMessage();

        // merged string representation
        String serialized = CustomStringJoiner.join(SEPARATOR, statusStr, initializationContextStr,
                ringMetadataStr, targetServerStr, responseMessage);

        logger.debug(join(" ", "Serialized message:", serialized));
        return new SerializedMessage(serialized);
    }

}
