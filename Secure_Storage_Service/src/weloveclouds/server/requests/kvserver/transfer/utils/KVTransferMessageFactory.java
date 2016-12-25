package weloveclouds.server.requests.kvserver.transfer.utils;


import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;

/**
 * A factory to create {@link KVTransferMessage} instances.
 * 
 * @author Benedek
 */
public class KVTransferMessageFactory {

    /**
     * Creates a {@link KVTransferMessage} with a {@link StatusType.RESPONSE_ERROR} field and with
     * an error message inside.
     * 
     * @param errorMessage the message that shall be transferred
     */
    public static KVTransferMessage createErrorKVTransferMessage(String errorMessage) {
        return new KVTransferMessage.Builder().status(StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
    }

    /**
     * Creates a {@link KVTransferMessage} with a {@link StatusType.RESPONSE_SUCESS} field.
     */
    public static KVTransferMessage createSuccessKVTransferMessage() {
        return new KVTransferMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

}

