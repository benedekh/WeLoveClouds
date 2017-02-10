package weloveclouds.commons.kvstore.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.KVMESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.KV_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.utils.StringUtils;


/**
 * A serializer which converts a {@link IKVMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit, Hunton
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedMessage, IKVMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVMessageSerializer.class);

    private ISerializer<AbstractXMLNode, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public SerializedMessage serialize(IKVMessage unserializedMessage) {
        LOGGER.debug("Serializing KVMessage.");

        StatusType status = unserializedMessage.getStatus();
        String message =
                new XMLRootNode.Builder().token(KVMESSAGE)
                        .addInnerNode(new XMLNode(STATUS,
                                status == null ? null : status.toString()))
                        .addInnerNode(
                                new XMLNode(KV_ENTRY,
                                        kvEntrySerializer
                                                .serialize(new KVEntry(unserializedMessage.getKey(),
                                                        unserializedMessage.getValue()))
                                                .toString()))
                        .build().toString();

        LOGGER.debug(StringUtils.join("", "KVMessage serialization finished: ", message));
        return new SerializedMessage(message);
    }
}
