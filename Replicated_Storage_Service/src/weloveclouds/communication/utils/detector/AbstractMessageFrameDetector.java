package weloveclouds.communication.utils.detector;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.utils.RegexpFactory;

public abstract class AbstractMessageFrameDetector {

    private String regexp;
    private Pattern pattern;

    public AbstractMessageFrameDetector(String tag) {
        this.regexp = RegexpFactory.createRegexpForTag(tag);
        this.pattern = Pattern.compile(regexp);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        result = prime * result + ((regexp == null) ? 0 : regexp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractMessageFrameDetector)) {
            return false;
        }
        AbstractMessageFrameDetector other = (AbstractMessageFrameDetector) obj;
        if (pattern == null) {
            if (other.pattern != null) {
                return false;
            }
        } else if (!pattern.equals(other.pattern)) {
            return false;
        }
        if (regexp == null) {
            if (other.regexp != null) {
                return false;
            }
        } else if (!regexp.equals(other.regexp)) {
            return false;
        }
        return true;
    }

    /**
     * Detects messages based on the detection pattern.
     * 
     * @param messages from where the messages have to be detected
     * @return a queue of the detected messages
     */
    public Queue<byte[]> detectMessages(byte[] messages) {
        String messagesAsStr = new String(messages, SerializedMessage.MESSAGE_ENCODING);
        Set<String> kvMessages = collectMatches(messagesAsStr);
        Queue<byte[]> result = new ArrayDeque<>();
        for (String kvMessage : kvMessages) {
            result.add(convertStringToByteArray(kvMessage));
        }
        return result;
    }

    /**
     * Removes messages based on the detection regular expression.
     * 
     * @param messages from where the messages have to be removed
     * @return a byte[] of those messages or message parts which were not recognized
     */
    public byte[] removeMessages(byte[] messages) {
        String messagesAsStr = new String(messages, SerializedMessage.MESSAGE_ENCODING);
        messagesAsStr = messagesAsStr.replaceAll(regexp, "");
        return convertStringToByteArray(messagesAsStr);
    }

    private byte[] convertStringToByteArray(String string) {
        return string.getBytes(SerializedMessage.MESSAGE_ENCODING);
    }

    /**
     * Collects the matches for the pattern in the source.
     * 
     * @param source where the patterns have to match
     * @return the matches
     */
    private Set<String> collectMatches(String source) {
        Set<String> matches = new HashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

}
