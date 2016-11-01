package weloveclouds.kvstore;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import weloveclouds.kvstore.IKVMessage.StatusType;


/**
 * Created by Benoit on 2016-11-01.
 */
public class KVMessageUtils {
    private static final String SEPARATOR = "-\r-";

    public static String convertMessageToString(KVMessage message){
        StatusType status = message.getStatus();
        String statusStr = status == null ? null : status.toString();
        return join(SEPARATOR, statusStr, message.getKey(), message.getValue());
    }
}
