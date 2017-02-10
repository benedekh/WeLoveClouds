package weloveclouds.commons.kvstore.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.KVADMIN_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.REMOVABLE_RANGE;
import static weloveclouds.commons.serialization.models.XMLTokens.REPLICAS;
import static weloveclouds.commons.serialization.models.XMLTokens.RESPONSE_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.TARGET_SERVER_INFO;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfosIterableSerializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link IKVAdminMessage} to a {@link SerializedMessage}.
 * 
 * @author Benedek, Hunton
 */
public class KVAdminMessageSerializer
        implements IMessageSerializer<SerializedMessage, IKVAdminMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVAdminMessageSerializer.class);

    private ISerializer<AbstractXMLNode, RingMetadata> metadataSerializer =
            new RingMetadataSerializer();
    private ISerializer<AbstractXMLNode, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();
    private ISerializer<AbstractXMLNode, Iterable<ServerConnectionInfo>> replicaConnectionInfosSerializer =
            new ServerConnectionInfosIterableSerializer();
    private ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public SerializedMessage serialize(IKVAdminMessage unserializedMessage) {
        LOGGER.debug("Serializing KVAdminMessage.");
        
        StatusType status = unserializedMessage.getStatus();
        String message = new XMLRootNode.Builder().token(KVADMIN_MESSAGE)
                .addInnerNode(new XMLNode(STATUS, status == null ? null : status.toString()))
                .addInnerNode(new XMLNode(RING_METADATA,
                        metadataSerializer.serialize(unserializedMessage.getRingMetadata())
                                .toString()))
                .addInnerNode(new XMLNode(TARGET_SERVER_INFO,
                        metadataPartSerializer.serialize(unserializedMessage.getTargetServerInfo())
                                .toString()))
                .addInnerNode(new XMLNode(REPLICAS, replicaConnectionInfosSerializer
                        .serialize(unserializedMessage.getReplicaConnectionInfos()).toString()))
                .addInnerNode(new XMLNode(REMOVABLE_RANGE,
                        hashRangeSerializer.serialize(unserializedMessage.getRemovableRange())
                                .toString()))
                .addInnerNode(
                        new XMLNode(RESPONSE_MESSAGE, unserializedMessage.getResponseMessage()))
                .build().toString();

        LOGGER.debug(StringUtils.join("", "KVAdminMessage serialization finished: ", message));
        return new SerializedMessage(message);
    }

}
