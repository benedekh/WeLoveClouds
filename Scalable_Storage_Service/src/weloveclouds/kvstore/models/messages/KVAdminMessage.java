package weloveclouds.kvstore.models.messages;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.server.models.ServerInitializationContext;

/**
 * Represents an administrative message between the ECS and the KVServer.
 * 
 * @author Benedek
 */
public class KVAdminMessage implements IKVAdminMessage {

    private StatusType status;
    private ServerInitializationContext initializationContext;
    private RingMetadata ringMetadata;
    private RingMetadataPart targetServerInfo;
    private String responseMessage;

    protected KVAdminMessage(Builder builder) {
        this.status = builder.status;
        this.ringMetadata = builder.ringMetadata;
        this.initializationContext = builder.initializationContext;
        this.targetServerInfo = builder.targetServerInfo;
        this.responseMessage = builder.responseMessage;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public ServerInitializationContext getInitializationContext() {
        return initializationContext;
    }

    @Override
    public RingMetadata getRingMetadata() {
        return ringMetadata;
    }

    @Override
    public RingMetadataPart getTargetServerInfo() {
        return targetServerInfo;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "Message status:",
                status == null ? null : status.toString(), ", Initialization context:",
                initializationContext == null ? null : initializationContext.toString(),
                ", Ring metadata:", ringMetadata == null ? null : ringMetadata.toString(),
                ", Target server info:",
                targetServerInfo == null ? null : targetServerInfo.toString(),
                ", Response message: ",
                responseMessage == null ? null : responseMessage.toString());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((initializationContext == null) ? 0 : initializationContext.hashCode());
        result = prime * result + ((responseMessage == null) ? 0 : responseMessage.hashCode());
        result = prime * result + ((ringMetadata == null) ? 0 : ringMetadata.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((targetServerInfo == null) ? 0 : targetServerInfo.hashCode());
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
        if (!(obj instanceof KVAdminMessage)) {
            return false;
        }
        KVAdminMessage other = (KVAdminMessage) obj;
        if (initializationContext == null) {
            if (other.initializationContext != null) {
                return false;
            }
        } else if (!initializationContext.equals(other.initializationContext)) {
            return false;
        }
        if (responseMessage == null) {
            if (other.responseMessage != null) {
                return false;
            }
        } else if (!responseMessage.equals(other.responseMessage)) {
            return false;
        }
        if (ringMetadata == null) {
            if (other.ringMetadata != null) {
                return false;
            }
        } else if (!ringMetadata.equals(other.ringMetadata)) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        if (targetServerInfo == null) {
            if (other.targetServerInfo != null) {
                return false;
            }
        } else if (!targetServerInfo.equals(other.targetServerInfo)) {
            return false;
        }
        return true;
    }

    /**
     * Builder pattern for creating a {@link KVAdminMessage} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private StatusType status;
        private ServerInitializationContext initializationContext;
        private RingMetadata ringMetadata;
        private RingMetadataPart targetServerInfo;
        private String responseMessage;

        public Builder status(StatusType status) {
            this.status = status;
            return this;
        }

        public Builder initializationContext(ServerInitializationContext initializationContext) {
            this.initializationContext = initializationContext;
            return this;
        }

        public Builder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        public Builder targetServerInfo(RingMetadataPart targetServerInfo) {
            this.targetServerInfo = targetServerInfo;
            return this;
        }

        public Builder responseMessage(String message) {
            this.responseMessage = message;
            return this;
        }

        public KVAdminMessage build() {
            return new KVAdminMessage(this);
        }
    }

}
