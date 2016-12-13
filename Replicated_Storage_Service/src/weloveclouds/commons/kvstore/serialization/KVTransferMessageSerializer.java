package weloveclouds.commons.kvstore.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.KVTRANSFER_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.PUTABLE_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.REMOVABLE_KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.RESPONSE_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNITS;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitsIterableSerializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;


/**
 * A serializer which converts a {@link KVTransferMessage} to a {@link SerializedMessage}.
 * 
 * @author Benedek
 */
public class KVTransferMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVTransferMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageSerializer.class);

    private ISerializer<AbstractXMLNode, Iterable<MovableStorageUnit>> storageUnitsSerializer =
            new MovableStorageUnitsIterableSerializer();
    private ISerializer<AbstractXMLNode, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public SerializedMessage serialize(KVTransferMessage unserializedMessage) {
        LOGGER.debug("Serializing KVTransferMessage.");
        StatusType status = unserializedMessage.getStatus();

        String message = new XMLRootNode.Builder().token(KVTRANSFER_MESSAGE)
                .addInnerNode(new XMLNode(STATUS, status == null ? null : status.toString()))
                .addInnerNode(new XMLNode(STORAGE_UNITS,
                        storageUnitsSerializer.serialize(unserializedMessage.getStorageUnits())
                                .toString()))
                .addInnerNode(new XMLNode(PUTABLE_ENTRY,
                        kvEntrySerializer.serialize(unserializedMessage.getPutableEntry())
                                .toString()))
                .addInnerNode(new XMLNode(REMOVABLE_KEY, unserializedMessage.getRemovableKey()))
                .addInnerNode(
                        new XMLNode(RESPONSE_MESSAGE, unserializedMessage.getResponseMessage()))
                .build().toString();

        LOGGER.debug(StringUtils.join("", "KVTransferMessage serialization finished: ", message));
        return new SerializedMessage(message);
    }

}
