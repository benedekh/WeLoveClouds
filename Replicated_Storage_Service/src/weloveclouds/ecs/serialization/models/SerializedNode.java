package weloveclouds.ecs.serialization.models;

import weloveclouds.client.utils.CustomStringJoiner;

import static weloveclouds.ecs.serialization.SerializationTokens.*;

/**
 * Created by Benoit on 2016-12-08.
 */
public class SerializedNode {
    private String serializedName;
    private String serializedConnectionInfos;
    private String serializedHashKey;
    private String serializedHashRange;
    private String serializedReplicas;
    private String serializedChildHashRanges;


    SerializedNode(Builder serializeNodeBuilder) {
        this.serializedName = serializeNodeBuilder.serializedName;
        this.serializedConnectionInfos = serializeNodeBuilder.serializedConnectionInfos;
        this.serializedHashKey = serializeNodeBuilder.serializedHashKey;
        this.serializedHashRange = serializeNodeBuilder.serializedHashRange;
        this.serializedReplicas = serializeNodeBuilder.serializedReplicas;
        this.serializedChildHashRanges = serializeNodeBuilder.serializedChildHashRanges;
    }

    public String toString() {
        return CustomStringJoiner.join("",
                NODE_START_TOKEN,
                NAME_START_TOKEN,
                serializedName,
                NAME_END_TOKEN,
                SERVER_CONNECTION_START_TOKEN,
                serializedConnectionInfos,
                SERVER_CONNECTION_END_TOKEN,
                HASH_KEY_START_TOKEN,
                serializedHashKey,
                HASH_KEY_END_TOKEN,
                HASH_RANGE_START_TOKEN,
                serializedHashRange,
                HASH_RANGE_END_TOKEN,
                REPLICAS_START_TOKEN,
                serializedReplicas,
                REPLICA_END_TOKEN,
                CHILD_HASH_RANGES_START_TOKEN,
                serializedChildHashRanges,
                CHILD_HASH_RANGES_END_TOKEN,
                NODE_END_TOKEN);
    }

    public static class Builder {
        private String serializedName;
        private String serializedConnectionInfos;
        private String serializedHashKey;
        private String serializedHashRange;
        private String serializedReplicas;
        private String serializedChildHashRanges;

        public Builder serializedName(String serializedName) {
            this.serializedName = serializedName;
            return this;
        }

        public Builder serializedConnectionInfos(String serializedConnectionInfos) {
            this.serializedConnectionInfos = serializedConnectionInfos;
            return this;
        }

        public Builder serializedHashKey(String serializedHashKey) {
            this.serializedHashKey = serializedHashKey;
            return this;
        }

        public Builder serializedHashRange(String serializedHashRange) {
            this.serializedHashRange = serializedHashRange;
            return this;
        }

        public Builder addSerializedReplica(String serializedReplicas) {
            this.serializedReplicas += serializedReplicas;
            return this;
        }

        public Builder addSerializedChildHashRange(String serializedChildHashRanges) {
            this.serializedChildHashRanges += serializedChildHashRanges;
            return this;
        }

        public SerializedNode build() {
            return new SerializedNode(this);
        }
    }
}
