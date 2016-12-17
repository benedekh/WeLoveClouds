package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;
import java.util.UUID;

import weloveclouds.communication.models.ServerConnectionInfo;

public interface IKVTransactionMessage {

    public enum StatusType {
        INIT, /* Are you ready for a new transaction? */
        COMMIT_READY, /* Are you ready for a commit? */
        COMMIT, /* Commit the transaction */
        ABORT, /* Abort the transaction */
        HELP, /* Send me the status of the transaction */
        RESPONSE_INIT_READY, /* I am ready for a new transaction */
        RESPONSE_COMMIT_READY, /* I am ready to commit */
        RESPONSE_COMMITTED, /* I committed the transaction */
        RESPONSE_ABORTED; /* I aborted the transaction */
    }

    public StatusType getStatus();

    public UUID getTransactionId();

    public Set<ServerConnectionInfo> getParticipantConnectionInfos();

    public IKVTransferMessage getTransferPayload();

}
