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

    private ISerializer<String, MovableStorageUnits> storageUnitsSerializer =
            new MovableStorageUnitsSerializer();

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public SerializedMessage serialize(KVTransferMessage unserializedMessage) {
        logger.debug("Serializing transfer message.");

        // original fields
        StatusType status = unserializedMessage.getStatus();
        MovableStorageUnits storageUnits = unserializedMessage.getStorageUnits();
        String responseMessage = unserializedMessage.getResponseMessage();

        // string representation
        String statusStr = status == null ? null : status.toString();
        String storageUnitsStr = storageUnitsSerializer.serialize(storageUnits);

        // merged string representation
        String serialized = join(SEPARATOR, statusStr, storageUnitsStr, responseMessage);

        logger.debug("Transfer message serialization finished.");
        return new SerializedMessage(serialized);
    }

}
