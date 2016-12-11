package weloveclouds.kvstore.serialization;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfosSetSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A serializer which converts a {@link KVAdminMessage} to a {@link SerializedMessage}.
 * 
 * @author Benedek
 */
public class KVAdminMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVAdminMessage> {

    public static final String PREFIX = "<KVADMIN>";
    public static final String SEPARATOR = "-ŁŁŁ-";
    public static final String POSTFIX = "</KVADMIN>";

    private static final Logger LOGGER = Logger.getLogger(KVAdminMessageSerializer.class);

    private ISerializer<String, RingMetadata> metadataSerializer = new RingMetadataSerializer();
    private ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();
    private ISerializer<String, Set<ServerConnectionInfo>> replicaConnectionInfosSerializer =
            new ServerConnectionInfosSetSerializer();
    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public SerializedMessage serialize(KVAdminMessage unserializedMessage) {
        LOGGER.debug("Serializing KVAdminMessage.");

        // original fields
        StatusType status = unserializedMessage.getStatus();
        RingMetadata ringMetadata = unserializedMessage.getRingMetadata();
        RingMetadataPart targetServerInfo = unserializedMessage.getTargetServerInfo();
        Set<ServerConnectionInfo> replicaConnectionInfos =
                unserializedMessage.getReplicaConnectionInfos();
        HashRange removableRange = unserializedMessage.getRemovableRange();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String ringMetadataStr = metadataSerializer.serialize(ringMetadata);
        String targetServerStr = metadataPartSerializer.serialize(targetServerInfo);
        String replicaConnectionInfosStr =
                replicaConnectionInfosSerializer.serialize(replicaConnectionInfos);
        String removeableRangeStr = hashRangeSerializer.serialize(removableRange);
        String responseMessage = unserializedMessage.getResponseMessage();

        // merged string representation
        String serialized = CustomStringJoiner.join(SEPARATOR, statusStr, ringMetadataStr,
                targetServerStr, replicaConnectionInfosStr, removeableRangeStr, responseMessage);
        String infixed = CustomStringJoiner.join("", PREFIX, serialized, POSTFIX);

        LOGGER.debug("KVAdminMessage serialization finished.");
        return new SerializedMessage(infixed);
    }

}
