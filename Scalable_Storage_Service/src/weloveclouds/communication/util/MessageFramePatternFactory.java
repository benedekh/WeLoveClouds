package weloveclouds.communication.util;

import java.util.regex.Pattern;

public class MessageFramePatternFactory {
    
    public static Pattern createKVMessagePattern() {
        return createPattern(MessageFrameRegexpFactory.createKVMessageRegexp());
    }

    public static Pattern createKVAdminMessagePattern() {
        return createPattern(MessageFrameRegexpFactory.createKVAdminMessageRegexp());
    }

    public static Pattern createKVTransferMessagePattern() {
        return createPattern(MessageFrameRegexpFactory.createKVTransferMessageRegexp());
    }

    private static Pattern createPattern(String regexp) {
        return Pattern.compile(regexp);
    }
}
