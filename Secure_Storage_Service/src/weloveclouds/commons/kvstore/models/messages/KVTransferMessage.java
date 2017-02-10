package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.proxy.KVTransferMessageProxy;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * Represents a message which transfers storage units between KVServers.
 * 
 * @author Benedek, Hunton
 */
public class KVTransferMessage implements IKVTransferMessage {

    private StatusType status;

    private Set<MovableStorageUnit> storageUnits;
    private KVEntry putableEntry;
    private String removableKey;
    private String responseMessage;

    protected KVTransferMessage(Builder builder) {
        this.status = builder.status;
        this.storageUnits = builder.storageUnits;
        this.putableEntry = builder.putableEntry;
        this.removableKey = builder.removableKey;
        this.responseMessage = builder.responseMessage;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public Set<MovableStorageUnit> getStorageUnits() {
        return storageUnits;
    }

    @Override
    public KVEntry getPutableEntry() {
        return putableEntry;
    }

    @Override
    public String getRemovableKey() {
        return removableKey;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((putableEntry == null) ? 0 : putableEntry.hashCode());
        result = prime * result + ((removableKey == null) ? 0 : removableKey.hashCode());
        result = prime * result + ((responseMessage == null) ? 0 : responseMessage.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((storageUnits == null) ? 0 : storageUnits.hashCode());
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
        if (obj instanceof KVTransferMessageProxy) {
            KVTransferMessageProxy other = (KVTransferMessageProxy) obj;
            return other.equals(this);
        }
        if (!(obj instanceof KVTransferMessage)) {
            return false;
        }
        KVTransferMessage other = (KVTransferMessage) obj;
        if (putableEntry == null) {
            if (other.putableEntry != null) {
                return false;
            }
        } else if (!putableEntry.equals(other.putableEntry)) {
            return false;
        }
        if (removableKey == null) {
            if (other.removableKey != null) {
                return false;
            }
        } else if (!removableKey.equals(other.removableKey)) {
            return false;
        }
        if (responseMessage == null) {
            if (other.responseMessage != null) {
                return false;
            }
        } else if (!responseMessage.equals(other.responseMessage)) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        if (storageUnits == null) {
            if (other.storageUnits != null) {
                return false;
            }
        } else if (!storageUnits.equals(other.storageUnits)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.join("", "{ Message status: ", status, ", Storage units: ",
                StringUtils.setToString(storageUnits), ", Putable entry: ", putableEntry,
                ", Removable key: ", removableKey, ", Response message: ", responseMessage, "}");
    }

    /**
     * Builder pattern for creating a {@link KVTransferMessage} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private StatusType status;
        private Set<MovableStorageUnit> storageUnits;
        private KVEntry putableEntry;
        private String removableKey;
        private String responseMessage;

        public Builder status(StatusType status) {
            this.status = status;
            return this;
        }

        public Builder storageUnits(Set<MovableStorageUnit> storageUnits) {
            this.storageUnits = storageUnits;
            return this;
        }

        public Builder putableEntry(KVEntry putableEntry) {
            this.putableEntry = putableEntry;
            return this;
        }

        public Builder removableKey(String removableKey) {
            this.removableKey = removableKey;
            return this;
        }

        public Builder responseMessage(String message) {
            this.responseMessage = message;
            return this;
        }

        public KVTransferMessage build() {
            return new KVTransferMessage(this);
        }
    }

}
