package weloveclouds.commons.kvstore.models.messages;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.proxy.KVMessageProxy;
import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a Key-value message that is transferred through the network (the entry and the message
 * type).
 * 
 * @author Benedek, Hunton
 */
public class KVMessage implements IKVMessage {

    private KVEntry entry;
    private StatusType status;

    protected KVMessage(Builder builder) {
        this.status = builder.status;
        this.entry = builder.entry;
    }

    @Override
    public String getKey() {
        return entry.getKey();
    }

    @Override
    public String getValue() {
        return entry.getValue();
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "Message status:", status, ", KVEntry:", entry);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entry == null) ? 0 : entry.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        if (obj instanceof KVMessageProxy) {
            KVMessageProxy other = (KVMessageProxy) obj;
            return other.equals(this);
        }
        if (!(obj instanceof KVMessage)) {
            return false;
        }
        KVMessage other = (KVMessage) obj;
        if (entry == null) {
            if (other.entry != null) {
                return false;
            }
        } else if (!entry.equals(other.entry)) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        return true;
    }

    /**
     * Builder pattern for creating a {@link KVMessage} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private KVEntry entry;
        private StatusType status;

        public Builder status(StatusType status) {
            this.status = status;
            return this;
        }

        public Builder key(String key) {
            if (entry == null) {
                entry = new KVEntry();
            }
            entry.setKey(key);
            return this;
        }

        public Builder value(String value) {
            if (entry == null) {
                entry = new KVEntry();
            }
            entry.setValue(value);
            return this;
        }

        public KVMessage build() {
            return new KVMessage(this);
        }
    }

}
