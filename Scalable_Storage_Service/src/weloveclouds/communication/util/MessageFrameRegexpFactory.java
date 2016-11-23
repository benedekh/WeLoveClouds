package weloveclouds.communication.util;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.KVTransferMessageSerializer;

public class MessageFrameRegexpFactory {

    public static String createKVMessageRegexp() {
        return createRegexp(KVMessageSerializer.PREFIX, KVMessageSerializer.POSTFIX);
    }

    public static String createKVAdminMessageRegexp() {
        return createRegexp(KVAdminMessageSerializer.PREFIX, KVAdminMessageSerializer.POSTFIX);
    }

    public static String createKVTransferMessageRegexp() {
        return createRegexp(KVTransferMessageSerializer.PREFIX,
                KVTransferMessageSerializer.POSTFIX);
    }

    private static String createRegexp(String prefix, String postfix) {
        return CustomStringJoiner.join(".*?", prefix, postfix);
    }

}
