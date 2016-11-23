package weloveclouds.communication.util;

import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

public class SerializedMessageValidator {

    private static final IMessageDeserializer<KVMessage, SerializedMessage> kvMessageDeserializer =
            new KVMessageDeserializer();
    private static final IMessageDeserializer<KVAdminMessage, SerializedMessage> kvAdminMessageDeserializer =
            new KVAdminMessageDeserializer();
    private static final IMessageDeserializer<KVTransferMessage, SerializedMessage> kvTransferMessageDeserializer =
            new KVTransferMessageDeserializer();

    public static boolean byteArrayRepresentsDeserializableMessage(byte[] message) {
        int numberOfFailedValidations = 0;

        try {
            kvMessageDeserializer.deserialize(message);
        } catch (DeserializationException ex) {
            numberOfFailedValidations++;
        }

        try {
            kvAdminMessageDeserializer.deserialize(message);
        } catch (DeserializationException ex) {
            numberOfFailedValidations++;
        }

        try {
            kvTransferMessageDeserializer.deserialize(message);
        } catch (DeserializationException ex) {
            numberOfFailedValidations++;
        }

        if (numberOfFailedValidations == 3) {
            return false;
        } else {
            return true;
        }
    }
}
