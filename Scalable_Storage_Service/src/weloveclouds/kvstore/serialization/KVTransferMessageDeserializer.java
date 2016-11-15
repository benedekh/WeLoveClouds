package weloveclouds.kvstore.serialization;

import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

public class KVTransferMessageDeserializer
        implements IMessageDeserializer<KVTransferMessage, SerializedMessage> {

    @Override
    public KVTransferMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVTransferMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        // TODO Auto-generated method stub
        return null;
    }

}
