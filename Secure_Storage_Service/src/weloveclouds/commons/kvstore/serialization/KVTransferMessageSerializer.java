package weloveclouds.commons.kvstore.serialization;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.serialization.helper.TransferMessageSerializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;


/**
 * A serializer which converts a {@link IKVTransferMessage} to a {@link SerializedMessage}.
 * 
 * @author Benedek, Hunton
 */
public class KVTransferMessageSerializer
        implements IMessageSerializer<SerializedMessage, IKVTransferMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageSerializer.class);

    private ISerializer<AbstractXMLNode, IKVTransferMessage> transferMessageSerializer =
            new TransferMessageSerializer();

    @Override
    public SerializedMessage serialize(IKVTransferMessage unserializedMessage) {
        LOGGER.debug("Serializing KVTransferMessage.");
        String message = transferMessageSerializer.serialize(unserializedMessage).toString();
        LOGGER.debug(StringUtils.join("", "KVTransferMessage serialization finished: ", message));
        return new SerializedMessage(message);
    }

}
