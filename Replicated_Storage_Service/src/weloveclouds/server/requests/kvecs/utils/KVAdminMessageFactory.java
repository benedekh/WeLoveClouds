package weloveclouds.server.requests.kvecs.utils;

import static weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR;
import static weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;

/**
 * A factory to create {@link KVAdminMessage} instances.
 * 
 * @author Benedek
 */
public class KVAdminMessageFactory {

    /**
     * Creates a {@link KVAdminMessage} with a {@link RESPONSE_ERROR} field and with an error
     * message inside.
     * 
     * @param errorMessage the message that shall be transferred
     */
    public static KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder().status(RESPONSE_ERROR).responseMessage(errorMessage)
                .build();
    }

    /**
     * Creates a {@link KVAdminMessage} with a {@link RESPONSE_SUCESS} field.
     */
    public static KVAdminMessage createSuccessKVAdminMessage() {
        return new KVAdminMessage.Builder().status(RESPONSE_SUCCESS).build();
    }

}
