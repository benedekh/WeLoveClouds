package weloveclouds.kvstore.models.messages;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.RangeInfo;
import weloveclouds.hashing.models.RangeInfos;

/**
 * Represents an administrative message between the ECS and the KVServer.
 * 
 * @author Benedek
 */
public class KVAdminMessage implements IKVAdminMessage {

    private StatusType status;

    private RangeInfos ringMetadata;
    private RangeInfo targetServerInfo;
    private String responseMessage;

    protected KVAdminMessage(KVAdminMessageBuilder builder) {
        this.status = builder.status;
        this.ringMetadata = builder.ringMetadata;
        this.targetServerInfo = builder.targetServerInfo;
        this.responseMessage = builder.responseMessage;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public RangeInfos getRingMetadata() {
        return ringMetadata;
    }

    @Override
    public RangeInfo getTargetServerInfo() {
        return targetServerInfo;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    public static class KVAdminMessageBuilder {
        private StatusType status;
        private RangeInfos ringMetadata;
        private RangeInfo targetServerInfo;
        private String responseMessage;

        public KVAdminMessageBuilder status(StatusType status) {
            this.status = status;
            return this;
        }

        public KVAdminMessageBuilder ringMetadata(RangeInfos metadata) {
            this.ringMetadata = metadata;
            return this;
        }

        public KVAdminMessageBuilder targetServerInfo(RangeInfo targetServerInfo) {
            this.targetServerInfo = targetServerInfo;
            return this;
        }

        public KVAdminMessageBuilder responseMessage(String message) {
            this.responseMessage = message;
            return this;
        }

        public KVAdminMessage build() {
            return new KVAdminMessage(this);
        }
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "Message status:",
                status == null ? null : status.toString(), "Metadata:",
                ringMetadata == null ? null : ringMetadata.toString(),
                "Target server info (IP, range):",
                targetServerInfo == null ? null : targetServerInfo.toString(), "Response message: ",
                responseMessage == null ? null : responseMessage.toString());
    }

}
