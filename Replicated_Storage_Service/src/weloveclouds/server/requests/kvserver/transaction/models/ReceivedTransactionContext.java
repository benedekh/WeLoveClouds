package weloveclouds.server.requests.kvserver.transaction.models;

import java.util.concurrent.locks.ReentrantLock;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbortRequest;

public class ReceivedTransactionContext {

    private volatile TransactionStatus transactionStatus;
    private IKVTransferMessage transferMessage;
    private volatile TimedAbortRequest timedAbortRequest;

    private ReentrantLock accessLock;

    protected ReceivedTransactionContext(Builder builder) {
        this.transactionStatus = builder.transactionStatus;
        this.transferMessage = builder.transferMessage;
        this.timedAbortRequest = builder.timedAbortRequest;
        this.accessLock = new ReentrantLock();
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public IKVTransferMessage getTransferMessage() {
        return transferMessage;
    }

    public ReentrantLock getLock() {
        return accessLock;
    }

    public void setCommitReady() {
        setStatus(TransactionStatus.COMMIT_READY);
    }

    public void setCommitted() {
        setCompleted(TransactionStatus.COMMITTED);
    }

    public void setAborted() {
        setCompleted(TransactionStatus.ABORTED);
    }

    public void scheduleAbortRequest() {
        try (CloseableLock lock = new CloseableLock(accessLock)) {
            timedAbortRequest.start();
        }
    }

    public void stopAbortRequest() {
        if (timedAbortRequest != null) {
            try (CloseableLock lock = new CloseableLock(accessLock)) {
                if (timedAbortRequest != null) {
                    timedAbortRequest.interrupt();
                    timedAbortRequest = null;
                }
            }
        }
    }

    private void setCompleted(TransactionStatus status) {
        if (!isCompleted()) {
            try (CloseableLock lock = new CloseableLock(accessLock)) {
                if (!isCompleted()) {
                    stopAbortRequest();
                    transferMessage = null;
                    setStatus(status);
                }
            }
        }
    }

    private void setStatus(TransactionStatus status) {
        transactionStatus = status;
    }

    private boolean isCompleted() {
        return transactionStatus == TransactionStatus.COMMITTED
                || transactionStatus == TransactionStatus.ABORTED;
    }

    public static class Builder {
        private TransactionStatus transactionStatus;
        private IKVTransferMessage transferMessage;
        private TimedAbortRequest timedAbortRequest;

        public Builder transactionStatus(TransactionStatus transactionStatus) {
            this.transactionStatus = transactionStatus;
            return this;
        }

        public Builder transferMessage(IKVTransferMessage transferMessage) {
            this.transferMessage = transferMessage;
            return this;
        }

        public Builder timedAbortRequest(TimedAbortRequest timedAbortRequest) {
            this.timedAbortRequest = timedAbortRequest;
            return this;
        }

        public ReceivedTransactionContext build() {
            return new ReceivedTransactionContext(this);
        }
    }

}
