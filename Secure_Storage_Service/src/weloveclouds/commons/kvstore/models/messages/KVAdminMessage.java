package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.models.messages.proxy.KVAdminMessageProxy;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;


/**
 * Represents an administrative message between the ECS and the KVServer.
 * 
 * @author Benedek, Hunton
 */
public class KVAdminMessage implements IKVAdminMessage {

    private StatusType status;
    private RingMetadata ringMetadata;
    private RingMetadataPart targetServerInfo;
    private Set<ServerConnectionInfo> replicaConnectionInfos;
    private HashRange removableRange;
    private String responseMessage;

    protected KVAdminMessage(Builder builder) {
        this.status = builder.status;
        this.ringMetadata = builder.ringMetadata;
        this.targetServerInfo = builder.targetServerInfo;
        this.replicaConnectionInfos = builder.replicaConnectionInfos;
        this.removableRange = builder.removableRange;
        this.responseMessage = builder.responseMessage;
    }

    @Override
    public StatusType getStatus() {
        return status;
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
    public Set<ServerConnectionInfo> getReplicaConnectionInfos() {
        return replicaConnectionInfos;
    }

    @Override
    public HashRange getRemovableRange() {
        return removableRange;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "Message status:", status, ", Removable range: ",
                removableRange, ", Ring metadata:", ringMetadata, ", Target server info:",
                targetServerInfo, ", Replica connection infos:",
                StringUtils.setToString(replicaConnectionInfos), ",  Response message: ",
                responseMessage);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((removableRange == null) ? 0 : removableRange.hashCode());
        result = prime * result
                + ((replicaConnectionInfos == null) ? 0 : replicaConnectionInfos.hashCode());
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
        if (obj instanceof KVAdminMessageProxy) {
            KVAdminMessageProxy other = (KVAdminMessageProxy) obj;
            return other.equals(this);
        }
        if (!(obj instanceof KVAdminMessage)) {
            return false;
        }
        KVAdminMessage other = (KVAdminMessage) obj;
        if (removableRange == null) {
            if (other.removableRange != null) {
                return false;
            }
        } else if (!removableRange.equals(other.removableRange)) {
            return false;
        }
        if (replicaConnectionInfos == null) {
            if (other.replicaConnectionInfos != null) {
                return false;
            }
        } else if (!replicaConnectionInfos.equals(other.replicaConnectionInfos)) {
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
        private RingMetadata ringMetadata;
        private RingMetadataPart targetServerInfo;
        private Set<ServerConnectionInfo> replicaConnectionInfos;
        private HashRange removableRange;
        private String responseMessage;

        public Builder status(StatusType status) {
            this.status = status;
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

        public Builder replicaConnectionInfos(Set<ServerConnectionInfo> replicaConnectionInfos) {
            this.replicaConnectionInfos = replicaConnectionInfos;
            return this;
        }

        public Builder removableRange(HashRange range) {
            this.removableRange = range;
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
