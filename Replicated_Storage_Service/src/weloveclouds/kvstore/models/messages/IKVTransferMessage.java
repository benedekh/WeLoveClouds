package weloveclouds.kvstore.models.messages;

import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * Represents a message which transfers storage units between KVServers.
 * 
 * @author Benedek
 */
public interface IKVTransferMessage {

    public enum StatusType {
        TRANSFER, /* Storage unit transfer - request */
        REMOVE_ENTRY_BY_KEY, /* Remove an entry denoted by its key - request */
        SUCCESS, /* Transfer was successful */
        ERROR /* Transfer was unsuccessful */
    }

    /**
     * @return a status string that is used to identify request types, response types and error
     *         types associated to the message
     */
    public StatusType getStatus();

    /**
     * @return the storage units
     */
    public MovableStorageUnits getStorageUnits();

    /**
     * @return the key whose entry shall be removed
     */
    public String getRemovableKey();

    /**
     * @return if the message is a response then the message text can be obtained here
     */
    public String getResponseMessage();

}
