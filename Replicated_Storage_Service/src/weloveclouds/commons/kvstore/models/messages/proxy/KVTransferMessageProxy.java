package weloveclouds.commons.kvstore.models.messages.proxy;

import java.util.Set;

import weloveclouds.commons.exceptions.IllegalAccessException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.store.models.MovableStorageUnit;


/**
 * Encapsulates a {@link #message} with method level access authentication based on the
 * {@link StatusType}.
 * 
 * @author Benedek
 */
public class KVTransferMessageProxy implements IKVTransferMessage {

    private IKVTransferMessage message;

    public KVTransferMessageProxy(IKVTransferMessage message) {
        this.message = message;
    }

    @Override
    public StatusType getStatus() {
        return message.getStatus();
    }

    @Override
    public Set<MovableStorageUnit> getStorageUnits() {
        switch (getStatus()) {
            case TRANSFER_ENTRIES:
                return message.getStorageUnits();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getStorageUnits");
        }
    }

    @Override
    public KVEntry getPutableEntry() {
        switch (getStatus()) {
            case PUT_ENTRY:
                return message.getPutableEntry();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getPutableEntry");
        }
    }

    @Override
    public String getRemovableKey() {
        switch (getStatus()) {
            case REMOVE_ENTRY_BY_KEY:
                return message.getRemovableKey();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getRemovableKey");
        }
    }

    @Override
    public String getResponseMessage() {
        switch (getStatus()) {
            case RESPONSE_ERROR:
            case RESPONSE_SUCCESS:
                return message.getResponseMessage();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getResponseMessage");
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
