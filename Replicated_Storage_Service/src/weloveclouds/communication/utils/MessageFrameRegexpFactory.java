package weloveclouds.communication.utils;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.models.XMLTokens;
import weloveclouds.commons.utils.StringUtils;

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
        return createRegexp(createOpeningTag(XMLTokens.KVMESSAGE),
                createClosingTag(XMLTokens.KVMESSAGE));
    }

    /**
     * @return a regular expression that detects the framing of a {@link KVAdminMessage}
     */
    public static String createKVAdminMessageRegexp() {
        return createRegexp(createOpeningTag(XMLTokens.KVADMIN_MESSAGE),
                createClosingTag(XMLTokens.KVADMIN_MESSAGE));
    }

    /**
     * @return a regular expression that detects the framing of a {@link KVTransferMessage}
     */
    public static String createKVTransferMessageRegexp() {
        return createRegexp(createOpeningTag(XMLTokens.KVTRANSFER_MESSAGE),
                createClosingTag(XMLTokens.KVTRANSFER_MESSAGE));
    }

    /**
     * Creates the following regexp: "prefix.*?postfix" where prefix and postfix are the two
     * parameters of this method.
     */
    private static String createRegexp(String prefix, String postfix) {
        return StringUtils.join(".*?", prefix, postfix);
    }

    private static String createOpeningTag(String tag) {
        return StringUtils.join("", "<", tag, ">");
    }

    private static String createClosingTag(String tag) {
        return StringUtils.join("", "</", tag, ">");
    }

}
