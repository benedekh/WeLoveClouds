package weloveclouds.communication.util;

import java.util.regex.Pattern;

/**
 * Factory that creates patterns ({@link Pattern}) to detect the framing of the KV*Messages.
 * 
 * @author Benedek
 */
public class MessageFramePatternFactory {

    /**
     * @return a pattern that detects the framing of a {@link KVMessage}
     */
    public static Pattern createKVMessagePattern() {
        return createPattern(MessageFrameRegexpFactory.createKVMessageRegexp());
    }

    /**
     * @return a pattern that detects the framing of a {@link KVAdminMessage}
     */
    public static Pattern createKVAdminMessagePattern() {
        return createPattern(MessageFrameRegexpFactory.createKVAdminMessageRegexp());
    }

    /**
     * @return a pattern that detects the framing of a {@link KVTransferMessage}
     */
    public static Pattern createKVTransferMessagePattern() {
        return createPattern(MessageFrameRegexpFactory.createKVTransferMessageRegexp());
    }

    /**
     * Creates a pattern from the parameter
     */
    private static Pattern createPattern(String regexp) {
        return Pattern.compile(regexp);
    }
}
