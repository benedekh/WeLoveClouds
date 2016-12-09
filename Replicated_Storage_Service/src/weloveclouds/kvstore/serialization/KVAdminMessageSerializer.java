package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.kvstore.serialization.helper.HashRangesWithRolesSerializer;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.kvstore.serialization.helper.ServerConnectionInfosSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.models.replication.HashRangesWithRoles;

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
    private ISerializer<String, HashRangesWithRoles> hashRangesWithRolesSerializer =
            new HashRangesWithRolesSerializer();
    private ISerializer<String, ServerConnectionInfos> replicaConnectionInfosSerializer =
            new ServerConnectionInfosSerializer();
    private ISerializer<String, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public SerializedMessage serialize(KVAdminMessage unserializedMessage) {
        LOGGER.debug(join(" ", "Serializing message:", unserializedMessage.toString()));

        // original fields
        StatusType status = unserializedMessage.getStatus();
        RingMetadata ringMetadata = unserializedMessage.getRingMetadata();
        RingMetadataPart targetServerInfo = unserializedMessage.getTargetServerInfo();
        HashRangesWithRoles rangesWithRoles = unserializedMessage.getManagedHashRangesWithRole();
        ServerConnectionInfos replicaConnectionInfos =
                unserializedMessage.getReplicaConnectionInfos();
        HashRange removableRange = unserializedMessage.getRemovableRange();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String ringMetadataStr = metadataSerializer.serialize(ringMetadata);
        String targetServerStr = metadataPartSerializer.serialize(targetServerInfo);
        String rangesWithRolesStr = hashRangesWithRolesSerializer.serialize(rangesWithRoles);
        String replicaConnectionInfosStr =
                replicaConnectionInfosSerializer.serialize(replicaConnectionInfos);
        String removeableRangeStr = hashRangeSerializer.serialize(removableRange);
        String responseMessage = unserializedMessage.getResponseMessage();

        // merged string representation
        String serialized = CustomStringJoiner.join(SEPARATOR, statusStr, ringMetadataStr,
                targetServerStr, rangesWithRolesStr, replicaConnectionInfosStr, removeableRangeStr,
                responseMessage);
        String prefixed = CustomStringJoiner.join("", PREFIX, serialized);
        String postfixed = CustomStringJoiner.join("", prefixed, POSTFIX);

        LOGGER.debug(join(" ", "Serialized message:", postfixed));
        return new SerializedMessage(postfixed);
    }

}
