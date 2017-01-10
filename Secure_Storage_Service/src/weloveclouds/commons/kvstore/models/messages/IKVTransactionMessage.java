package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;
import java.util.UUID;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Represents a transaction message.
 * 
 * @author Benedek
 */
public interface IKVTransactionMessage {

    public enum StatusType {
        INIT, /* Are you ready for a new transaction? */
        COMMIT_READY, /* Are you ready for a commit? */
        COMMIT, /* Commit the transaction */
        ABORT, /* Abort the transaction */
        HELP, /* Ask for help regarding the transaction status */
        RESPONSE_INIT_READY, /* I am ready for a new transaction */
        RESPONSE_GENERATE_NEW_ID, /* Not ready for the transaction, need transaction ID is needed */
        RESPONSE_COMMIT_READY, /* I am ready to commit */
        RESPONSE_COMMITTED, /* I committed the transaction */
        RESPONSE_ABORTED, /* I aborted the transaction */
        RESPONSE_UNKNOWN_ID; /* Unknown transaction ID */
    }

    public StatusType getStatus();

    public UUID getTransactionId();

    public IKVTransferMessage getTransferPayload();

    public Set<ServerConnectionInfo> getOtherParticipants();

}
