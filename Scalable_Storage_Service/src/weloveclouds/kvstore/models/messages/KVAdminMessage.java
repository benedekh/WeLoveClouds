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

    protected KVAdminMessage(KVAdminMessageBuilder builder) {
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

    public static class KVAdminMessageBuilder {
        private StatusType status;
        private ServerInitializationContext initializationContext;
        private RingMetadata ringMetadata;
        private RingMetadataPart targetServerInfo;
        private String responseMessage;

        public KVAdminMessageBuilder status(StatusType status) {
            this.status = status;
            return this;
        }

        public KVAdminMessageBuilder initializationContext(
                ServerInitializationContext initializationContext) {
            this.initializationContext = initializationContext;
            return this;
        }

        public KVAdminMessageBuilder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        public KVAdminMessageBuilder targetServerInfo(RingMetadataPart targetServerInfo) {
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
                status == null ? null : status.toString(), "Initialization context:",
                initializationContext == null ? null : initializationContext.toString(),
                "Ring metadata:", ringMetadata == null ? null : ringMetadata.toString(),
                "Target server info:",
                targetServerInfo == null ? null : targetServerInfo.toString(), "Response message: ",
                responseMessage == null ? null : responseMessage.toString());
    }



}
