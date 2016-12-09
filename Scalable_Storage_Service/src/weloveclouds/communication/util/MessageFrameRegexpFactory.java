package weloveclouds.communication.util;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;
import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;

/**
 * Factory that creates regular expressions to detect the framing of the KV*Messages.
 * 
 * @author Benedek
 */
public class MessageFrameRegexpFactory {

    /**
     * @return a regular expression that detects the framing of a {@link KVMessage}
     */
    public static String createKVMessageRegexp() {
        return createRegexp(KVMessageSerializer.PREFIX, KVMessageSerializer.POSTFIX);
    }

    /**
     * @return a regular expression that detects the framing of a {@link KVAdminMessage}
     */
    public static String createKVAdminMessageRegexp() {
        return createRegexp(KVAdminMessageSerializer.PREFIX, KVAdminMessageSerializer.POSTFIX);
    }

    /**
     * @return a regular expression that detects the framing of a {@link KVTransferMessage}
     */
    public static String createKVTransferMessageRegexp() {
        return createRegexp(KVTransferMessageSerializer.PREFIX,
                KVTransferMessageSerializer.POSTFIX);
    }

    /**
     * Creates the following regexp: "prefix.*?postfix" where prefix and postfix are the two
     * parameters of this method.
     */
    private static String createRegexp(String prefix, String postfix) {
        return CustomStringJoiner.join(".*?", prefix, postfix);
    }

}
