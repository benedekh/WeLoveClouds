package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Encapsulates a {@link #message} with method level access authentication based on the
 * {@link StatusType}.
 * 
 * @author Benedek
 */
public class KVAdminMessageProxy implements IKVAdminMessage {

    private KVAdminMessage message;

    public KVAdminMessageProxy(KVAdminMessage message) {
        this.message = message;
    }

    @Override
    public StatusType getStatus() {
        return message.getStatus();
    }

    @Override
    public RingMetadata getRingMetadata() {
        StatusType status = message.getStatus();
        switch (status) {
            case INITKVSERVER:
            case UPDATE:
                return message.getRingMetadata();
            default:
                throw new weloveclouds.commons.exceptions.IllegalAccessException(status.toString(),
                        "getRingMetadata");
        }
    }

    @Override
    public RingMetadataPart getTargetServerInfo() {
        StatusType status = message.getStatus();
        switch (status) {
            case INITKVSERVER:
            case COPYDATA:
            case MOVEDATA:
            case UPDATE:
                return message.getTargetServerInfo();
            default:
                throw new weloveclouds.commons.exceptions.IllegalAccessException(status.toString(),
                        "getTargetServerInfo");
        }
    }

    @Override
    public Set<ServerConnectionInfo> getReplicaConnectionInfos() {
        StatusType status = message.getStatus();
        switch (status) {
            case INITKVSERVER:
            case UPDATE:
                return message.getReplicaConnectionInfos();
            default:
                throw new weloveclouds.commons.exceptions.IllegalAccessException(status.toString(),
                        "getReplicaConnectionInfos");
        }
    }

    @Override
    public HashRange getRemovableRange() {
        StatusType status = message.getStatus();
        switch (status) {
            case REMOVERANGE:
                return message.getRemovableRange();
            default:
                throw new weloveclouds.commons.exceptions.IllegalAccessException(status.toString(),
                        "getRemovableRange");
        }
    }

    @Override
    public String getResponseMessage() {
        StatusType status = message.getStatus();
        switch (status) {
            case RESPONSE_ERROR:
            case RESPONSE_SUCCESS:
                return message.getResponseMessage();
            default:
                throw new weloveclouds.commons.exceptions.IllegalAccessException(status.toString(),
                        "getResponseMessage");
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
        if (obj instanceof KVAdminMessage) {
            KVAdminMessage other = (KVAdminMessage) obj;
            return other.equals(message);
        }
        if (!(obj instanceof KVAdminMessageProxy)) {
            return false;
        }
        KVAdminMessageProxy other = (KVAdminMessageProxy) obj;
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
