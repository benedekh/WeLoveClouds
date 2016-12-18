package weloveclouds.communication.utils;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransactionMessage;
import weloveclouds.communication.utils.detector.AbstractMessageFrameDetector;
import weloveclouds.communication.utils.detector.KVAdminMessageFrameDetector;
import weloveclouds.communication.utils.detector.KVMessageFrameDetector;
import weloveclouds.communication.utils.detector.KVTransactionMessageFrameDetector;

/**
 * Utility class which detects message frames ({@link KVMessage}, {@link KVAdminMessage},
 * {@link KVTransactionMessage}) from byte[].
 * 
 * @author Benedek
 */
public class MessageFramesDetector {

    private Queue<byte[]> messages;
    private Set<AbstractMessageFrameDetector> frameDetectors;

    public MessageFramesDetector() {
        this.messages = new ArrayDeque<>();
        this.frameDetectors = new HashSet<>();

        this.frameDetectors.add(new KVAdminMessageFrameDetector());
        this.frameDetectors.add(new KVTransactionMessageFrameDetector());
        this.frameDetectors.add(new KVMessageFrameDetector());
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
        for (AbstractMessageFrameDetector frameDetector : frameDetectors) {
            this.messages.addAll(frameDetector.detectMessages(messages));
        }
        for (AbstractMessageFrameDetector frameDetector : frameDetectors) {
            messages = frameDetector.removeMessages(messages);
        }
        return messages;
    }

}
