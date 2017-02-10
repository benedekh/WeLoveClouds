package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * Represents a message which transfers storage units between KVServers.
 * 
 * @author Hunton
 */
public interface IKVTransferMessage {

    public enum StatusType {
        TRANSFER_ENTRIES, /* Storage unit transfer - request */
        PUT_ENTRY, /* Put a new entry - request */
        REMOVE_ENTRY_BY_KEY, /* Remove an entry denoted by its key - request */
        RESPONSE_SUCCESS, /* Transfer was successful */
        RESPONSE_ERROR /* Transfer was unsuccessful */
    }

    /**
     * @return a status string that is used to identify request types, response types and error
     *         types associated to the message
     */
    public StatusType getStatus();

    /**
     * @return the storage units
     */
    public Set<MovableStorageUnit> getStorageUnits();

    /**
     * @return the entry that shall be put in the data access service
     */
    public KVEntry getPutableEntry();

    /**
     * @return the key whose entry shall be removed
     */
    public String getRemovableKey();

    /**
     * @return if the message is a response then the message text can be obtained here
     */
    public String getResponseMessage();

}
