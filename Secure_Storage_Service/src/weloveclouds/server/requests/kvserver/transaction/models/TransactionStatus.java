package weloveclouds.server.requests.kvserver.transaction.models;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;

/**
 * Status of the transaction on the receiver side.
 * 
 * @author Benedek
 */
public enum TransactionStatus {

    INIT, COMMIT_READY, COMMITTED, ABORTED;

    public StatusType getResponseType() {
        switch (this) {
            case INIT:
                return StatusType.RESPONSE_INIT_READY;
            case COMMIT_READY:
                return StatusType.RESPONSE_COMMIT_READY;
            case COMMITTED:
                return StatusType.RESPONSE_COMMITTED;
            case ABORTED:
                return StatusType.RESPONSE_ABORTED;
            default:
                return null;
        }
    }

}
