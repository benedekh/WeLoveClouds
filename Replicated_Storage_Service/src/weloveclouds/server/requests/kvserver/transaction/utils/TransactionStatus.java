package weloveclouds.server.requests.kvserver.transaction.utils;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;

public enum TransactionStatus {

    INIT, COMMIT_READY, COMMITTED, ABORTED, HELP_NEEDED;

    public StatusType getResponseType() {
        switch (this) {
            case ABORTED:
                return StatusType.RESPONSE_ABORTED;
            case COMMIT_READY:
                return StatusType.RESPONSE_COMMIT_READY;
            case COMMITTED:
                return StatusType.RESPONSE_COMMITTED;
            case INIT:
                return StatusType.RESPONSE_INIT_READY;
            case HELP_NEEDED:
                return StatusType.HELP;
            default:
                return null;
        }
    }

}
