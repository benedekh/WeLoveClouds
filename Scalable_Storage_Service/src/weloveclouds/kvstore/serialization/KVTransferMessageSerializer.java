package weloveclouds.kvstore.serialization;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.kvstore.serialization.helper.MovableStorageUnitsSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.store.models.MovableStorageUnits;

public class KVTransferMessageSerializer
        implements IMessageSerializer<SerializedMessage, KVTransferMessage> {

    public static final String SEPARATOR = "-\r\r-";
    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageSerializer.class);

    private ISerializer<String, MovableStorageUnits> storageUnitsSerializer =
            new MovableStorageUnitsSerializer();

    @Override
    public SerializedMessage serialize(KVTransferMessage unserializedMessage) {
        LOGGER.debug("Serializing transfer message.");

        // original fields
        StatusType status = unserializedMessage.getStatus();
        MovableStorageUnits storageUnits = unserializedMessage.getStorageUnits();
        String responseMessage = unserializedMessage.getResponseMessage();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String storageUnitsStr = storageUnitsSerializer.serialize(storageUnits);

        // merged string representation
        String serialized = join(SEPARATOR, statusStr, storageUnitsStr, responseMessage);

        LOGGER.debug("Transfer message serialization finished.");
        return new SerializedMessage(serialized);
    }

}
