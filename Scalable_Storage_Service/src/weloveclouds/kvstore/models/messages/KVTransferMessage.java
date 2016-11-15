package weloveclouds.kvstore.models.messages;

import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * Represents a message which transfers storage units between KVServers.
 * 
 * @author Benedek
 */
public class KVTransferMessage implements IKVTransferMessage {

    private StatusType status;

    private MovableStorageUnits storageUnits;
    private String responseMessage;

    protected KVTransferMessage(KVTransferMessageBuilder builder) {
        this.status = builder.status;
        this.storageUnits = builder.storageUnits;
        this.responseMessage = builder.responseMessage;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public MovableStorageUnits getStorageUnits() {
        return storageUnits;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    public static class KVTransferMessageBuilder {
        private StatusType status;
        private MovableStorageUnits storageUnits;
        private String responseMessage;

        public KVTransferMessageBuilder status(StatusType status) {
            this.status = status;
            return this;
        }

        public KVTransferMessageBuilder storageUnits(MovableStorageUnits storageUnits) {
            this.storageUnits = storageUnits;
            return this;
        }

        public KVTransferMessageBuilder responseMessage(String message) {
            this.responseMessage = message;
            return this;
        }

        public KVTransferMessage build() {
            return new KVTransferMessage(this);
        }
    }

}
