package weloveclouds.commons.kvstore.models.messages.proxy;

import weloveclouds.commons.exceptions.IllegalAccessException;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.KVMessage;

/**
 * Encapsulates a {@link #message} with method level access authentication based on the
 * {@link StatusType}.
 * 
 * @author Benedek
 */
public class KVMessageProxy implements IKVMessage {

    private KVMessage message;

    public KVMessageProxy(KVMessage message) {
        this.message = message;
    }

    @Override
    public String getKey() {
        switch (getStatus()) {
            case PUT:
            case DELETE:
            case GET:
                return message.getKey();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getKey");
        }
    }

    @Override
    public String getValue() {
        switch (getStatus()) {
            case DELETE_ERROR:
            case DELETE_SUCCESS:
            case PUT_ERROR:
            case PUT_SUCCESS:
            case PUT_UPDATE:
            case GET_ERROR:
            case GET_SUCCESS:
            case SERVER_NOT_RESPONSIBLE:
            case SERVER_STOPPED:
            case SERVER_WRITE_LOCK:
            case PUT:
                return message.getValue();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getValue");
        }
    }

    @Override
    public StatusType getStatus() {
        return message.getStatus();
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
        if (obj instanceof KVMessage) {
            KVMessage other = (KVMessage) obj;
            return message.equals(other);
        }
        if (!(obj instanceof KVMessageProxy)) {
            return false;
        }
        KVMessageProxy other = (KVMessageProxy) obj;
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
