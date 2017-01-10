package weloveclouds.commons.kvstore.models.messages.proxy;

import java.util.Set;
import java.util.UUID;

import weloveclouds.commons.exceptions.IllegalAccessException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransactionMessage;
import weloveclouds.communication.models.ServerConnectionInfo;

public class KVTransactionMessageProxy implements IKVTransactionMessage {

    private IKVTransactionMessage message;

    public KVTransactionMessageProxy(IKVTransactionMessage message) {
        this.message = message;
    }

    @Override
    public StatusType getStatus() {
        return message.getStatus();
    }

    @Override
    public UUID getTransactionId() {
        return message.getTransactionId();
    }

    @Override
    public IKVTransferMessage getTransferPayload() {
        switch (getStatus()) {
            case INIT:
                return message.getTransferPayload();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getTransferPayload");
        }
    }

    @Override
    public Set<ServerConnectionInfo> getOtherParticipants() {
        switch (getStatus()) {
            case INIT:
                return message.getOtherParticipants();
            default:
                throw new IllegalAccessException(getStatus().toString(), "getOtherParticipants");
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
        if (obj instanceof KVTransactionMessage) {
            KVTransactionMessage other = (KVTransactionMessage) obj;
            return other.equals(message);
        }
        if (!(obj instanceof KVTransactionMessageProxy)) {
            return false;
        }
        KVTransactionMessageProxy other = (KVTransactionMessageProxy) obj;
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
