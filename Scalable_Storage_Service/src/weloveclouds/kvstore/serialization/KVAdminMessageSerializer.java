package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RangeInfo;
import weloveclouds.hashing.models.RangeInfos;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * An exact serializer which converts a {@link KVAdminMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVAdminMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVAdminMessage> {

    private static final String SEPARATOR = "-\r-";

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public SerializedMessage serialize(KVAdminMessage unserializedMessage) {
        logger.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        // original fields
        StatusType status = unserializedMessage.getStatus();
        RangeInfos ringMetadata = unserializedMessage.getRingMetadata();
        RangeInfo targetServerInfo = unserializedMessage.getTargetServerInfo();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String ringMetadataStr = ringMetadata == null ? null : ringMetadata.toStringWithDelimiter();
        String targetServerStr =
                targetServerInfo == null ? null : targetServerInfo.toStringWithDelimiter();
        String responseMessage = unserializedMessage.getResponseMessage();

        // merged string representation
        String serialized = CustomStringJoiner.join(SEPARATOR, statusStr, ringMetadataStr,
                targetServerStr, responseMessage);
        
        logger.debug(join(" ", "Serialized message:", serialized));
        return new SerializedMessage(serialized);
    }

}
