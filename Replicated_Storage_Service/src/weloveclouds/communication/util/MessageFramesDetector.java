package weloveclouds.communication.util;

import static weloveclouds.communication.util.MessageFrameRegexpFactory.createKVMessageRegexp;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;

/**
 * Utility class which detects message frames ({@link KVMessage}, {@link KVAdminMessage},
 * {@link KVTransferMessage}) from byte[].
 * 
 * @author Benedek
 */
public class MessageFramesDetector {

    private Queue<byte[]> messages;

    public MessageFramesDetector() {
        this.messages = new ArrayDeque<>();
    }

    /**
     * @return retrieve the first message from the queue
     */
    public byte[] getMessage() {
        return messages.poll();
    }

    /**
     * @return true if there is any recognized message in the queue
     */
    public boolean containsMessage() {
        return !messages.isEmpty();
    }

    /**
     * Detects and removes valid messages based on their framing in the byte[] message parameter.
     * 
     * @return a byte[] that contains the rest bytes which were not recognized being part of a valid
     *         message
     */
    public byte[] fillMessageQueue(byte[] messages) {
        this.messages.addAll(
                detectMessages(messages, MessageFramePatternFactory.createKVMessagePattern()));
        this.messages.addAll(
                detectMessages(messages, MessageFramePatternFactory.createKVAdminMessagePattern()));
        this.messages.addAll(detectMessages(messages,
                MessageFramePatternFactory.createKVTransferMessagePattern()));

        messages = removeMessages(messages, MessageFrameRegexpFactory.createKVMessageRegexp());
        messages = removeMessages(messages, MessageFrameRegexpFactory.createKVAdminMessageRegexp());
        messages =
                removeMessages(messages, MessageFrameRegexpFactory.createKVTransferMessageRegexp());
        return messages;
    }

    /**
     * Detects messages based on the detection pattern.
     * 
     * @param messages from where the messages have to be detected
     * @param pattern that is used to recognize the messages
     * @return a queue of the detected messages
     */
    private Queue<byte[]> detectMessages(byte[] messages, Pattern pattern) {
        String messagesAsStr = new String(messages, SerializedMessage.MESSAGE_ENCODING);
        Set<String> kvMessages = collectMatches(pattern, messagesAsStr);
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
     * @param regexp that is used for recognizing the respective messages
     * @return a byte[] of those messages or message parts which were not recognized
     */
    private byte[] removeMessages(byte[] messages, String regexp) {
        String messagesAsStr = new String(messages, SerializedMessage.MESSAGE_ENCODING);
        messagesAsStr = messagesAsStr.replaceAll(createKVMessageRegexp(), "");
        return convertStringToByteArray(messagesAsStr);
    }


    private byte[] convertStringToByteArray(String string) {
        return string.getBytes(SerializedMessage.MESSAGE_ENCODING);
    }

    /**
     * Collects the matches for the pattern in the source.
     * 
     * @param source where the patterns have to match
     * @param pattern used for deciding if we have a match
     * @return the matches
     */
    private Set<String> collectMatches(Pattern pattern, String source) {
        Set<String> matches = new HashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }
}
