package weloveclouds.kvstore.utils;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.kvstore.models.IKVMessage.StatusType;


/**
 * Utility class for {@link KVMessage}}.
 * 
 * @author Benedek
 */
public class KVMessageUtils {
    private static final String SEPARATOR = "-\r-";

    /**
     * Converts a {@link KVMessage} to a string for serialization purposes.
     */
    public static String convertMessageToString(KVMessage message) {
        StatusType status = message.getStatus();
        String statusStr = status == null ? null : status.toString();
        return join(SEPARATOR, statusStr, message.getKey(), message.getValue());
    }
}
