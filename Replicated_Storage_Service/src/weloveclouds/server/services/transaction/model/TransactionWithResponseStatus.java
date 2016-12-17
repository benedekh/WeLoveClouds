package weloveclouds.server.services.transaction.model;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;

public class TransactionWithResponseStatus {

    private SenderTransaction transaction;
    private StatusType responseStatus;

    public TransactionWithResponseStatus(SenderTransaction transaction, StatusType responseStatus) {
        this.transaction = transaction;
        this.responseStatus = responseStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((responseStatus == null) ? 0 : responseStatus.hashCode());
        result = prime * result + ((transaction == null) ? 0 : transaction.hashCode());
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
        if (!(obj instanceof TransactionWithResponseStatus)) {
            return false;
        }
        TransactionWithResponseStatus other = (TransactionWithResponseStatus) obj;
        if (responseStatus != other.responseStatus) {
            return false;
        }
        if (transaction == null) {
            if (other.transaction != null) {
                return false;
            }
        } else if (!transaction.equals(other.transaction)) {
            return false;
        }
        return true;
    }

    public SenderTransaction getTransaction() {
        return transaction;
    }

    public StatusType getResponseStatus() {
        return responseStatus;
    }

}
