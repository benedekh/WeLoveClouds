package weloveclouds.communication.util;

import static weloveclouds.communication.util.MessageFrameRegexpFactory.createKVMessageRegexp;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weloveclouds.kvstore.serialization.models.SerializedMessage;

public class MessageFramesDetector {

    private Queue<byte[]> messages;

    public MessageFramesDetector() {
        this.messages = new ArrayDeque<>();
    }

    public byte[] getMessage() {
        return messages.poll();
    }

    public boolean containsMessage() {
        return !messages.isEmpty();
    }

    public byte[] fillMessageStore(byte[] messages) {
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

    private Queue<byte[]> detectMessages(byte[] messages, Pattern pattern) {
        String messagesAsStr = new String(messages, SerializedMessage.MESSAGE_ENCODING);
        Set<String> kvMessages = collectMatches(pattern, messagesAsStr);
        Queue<byte[]> result = new ArrayDeque<>();
        moveStringsToByteArray(kvMessages, result);
        return result;
    }

    private byte[] removeMessages(byte[] messages, String regexp) {
        String messagesAsStr = new String(messages, SerializedMessage.MESSAGE_ENCODING);
        messagesAsStr = messagesAsStr.replaceAll(createKVMessageRegexp(), "");
        return convertStringToByteArray(messagesAsStr);
    }

    private void moveStringsToByteArray(Set<String> source, Queue<byte[]> target) {
        for (String s : source) {
            target.add(convertStringToByteArray(s));
        }
    }

    private byte[] convertStringToByteArray(String string) {
        return string.getBytes(SerializedMessage.MESSAGE_ENCODING);
    }

    private Set<String> collectMatches(Pattern pattern, String data) {
        Set<String> matches = new HashSet<>();
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }
}
