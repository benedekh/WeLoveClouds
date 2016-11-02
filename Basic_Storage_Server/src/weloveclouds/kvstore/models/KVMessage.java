package weloveclouds.kvstore.models;

import weloveclouds.client.utils.CustomStringJoiner;

public class KVMessage implements IKVMessage {

    private KVEntry entry;
    private StatusType status;

    protected KVMessage(KVMessageBuilder builder) {
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

    public static class KVMessageBuilder {
        private KVEntry entry;
        private StatusType status;

        public KVMessageBuilder status(StatusType status) {
            this.status = status;
            return this;
        }

        public KVMessageBuilder key(String key) {
            if (entry == null) {
                entry = new KVEntry();
            }
            entry.setKey(key);
            return this;
        }

        public KVMessageBuilder value(String value) {
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

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "Message status:",
                status == null ? "null" : status.toString(), "KVEntry:",
                entry == null ? "null" : entry.toString());
    }

}
