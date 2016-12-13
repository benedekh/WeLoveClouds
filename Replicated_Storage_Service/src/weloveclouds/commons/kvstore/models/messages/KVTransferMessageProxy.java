package weloveclouds.commons.kvstore.models.messages;

import weloveclouds.commons.exceptions.IllegalAccessException;
import java.util.Set;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.server.store.models.MovableStorageUnit;


/**
 * Encapsulates a {@link #message} with method level access authentication based on the
 * {@link StatusType}.
 * 
 * @author Benedek
 */
public class KVTransferMessageProxy implements IKVTransferMessage {

    private KVTransferMessage message;

    public KVTransferMessageProxy(KVTransferMessage message) {
        this.message = message;
    }

    @Override
    public StatusType getStatus() {
        return message.getStatus();
    }

    @Override
    public Set<MovableStorageUnit> getStorageUnits() {
        StatusType status = message.getStatus();
        switch (status) {
            case TRANSFER_ENTRIES:
                return message.getStorageUnits();
            default:
                throw new IllegalAccessException(status.toString(), "getStorageUnits");
        }
    }

    @Override
    public KVEntry getPutableEntry() {
        StatusType status = message.getStatus();
        switch (status) {
            case PUT_ENTRY:
                return message.getPutableEntry();
            default:
                throw new IllegalAccessException(status.toString(), "getPutableEntry");
        }
    }

    @Override
    public String getRemovableKey() {
        StatusType status = message.getStatus();
        switch (status) {
            case REMOVE_ENTRY_BY_KEY:
                return message.getRemovableKey();
            default:
                throw new IllegalAccessException(status.toString(), "getRemovableKey");
        }
    }

    @Override
    public String getResponseMessage() {
        StatusType status = message.getStatus();
        switch (status) {
            case RESPONSE_ERROR:
            case RESPONSE_SUCCESS:
                return message.getResponseMessage();
            default:
                throw new weloveclouds.commons.exceptions.IllegalAccessException(status.toString(),
                        "getResponseMessage");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
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
        if (obj instanceof KVTransferMessage) {
            KVTransferMessage other = (KVTransferMessage) obj;
            return other.equals(message);
        }
        if (!(obj instanceof KVTransferMessageProxy)) {
            return false;
        }
        KVTransferMessageProxy other = (KVTransferMessageProxy) obj;
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return message.toString();
    }

}
