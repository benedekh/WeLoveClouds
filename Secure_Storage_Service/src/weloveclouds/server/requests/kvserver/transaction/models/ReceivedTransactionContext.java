package weloveclouds.server.requests.kvserver.transaction.models;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.requests.kvserver.transaction.utils.ITransactionRestorationRequest;

/**
 * The context information of a transaction which is stored on the receiver side (where the
 * transaction itself is executed).
 * 
 * @author Benedek
 */
public class ReceivedTransactionContext {

    private final UUID transactionId;
    private final ReentrantLock accessLock;

    private volatile TransactionStatus transactionStatus;
    private IKVTransferMessage transferMessage;
    private Set<ServerConnectionInfo> otherParticipants;
    private volatile ITransactionRestorationRequest timedRestoration;

    protected ReceivedTransactionContext(Builder builder) {
        this.transactionId = builder.transactionId;
        this.transactionStatus = builder.transactionStatus;
        this.transferMessage = builder.transferMessage;
        this.otherParticipants = builder.otherParticipants;
        this.timedRestoration = builder.timedRestoration;
        this.accessLock = new ReentrantLock();
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public IKVTransferMessage getTransferMessage() {
        return transferMessage;
    }

    public Set<ServerConnectionInfo> getOtherParticipants() {
        return Collections.unmodifiableSet(otherParticipants);
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

    /**
     * Sets the helper restoration protocol which shall be executed if the transaction is not
     * completed in a given time frame.
     */
    public void setTimedRestoration(ITransactionRestorationRequest timedRestoration) {
        this.timedRestoration = timedRestoration;
    }

    /**
     * Schedules the helper restoration protocol which shall be executed if the transaction is not
     * completed in a given time frame.
     */
    public void scheduleTimedRestoration() {
        if (timedRestoration != null) {
            try (CloseableLock lock = new CloseableLock(accessLock)) {
                if (timedRestoration != null) {
                    timedRestoration.schedule();
                }
            }
        }
    }

    /**
     * Stops the helper restoration protocol which shall be executed if the transaction is not
     * completed in a given time frame.
     */
    public void stopTimedRestoration() {
        if (timedRestoration != null) {
            try (CloseableLock lock = new CloseableLock(accessLock)) {
                if (timedRestoration != null) {
                    timedRestoration.unschedule();
                    timedRestoration = null;
                }
            }
        }
    }

    private void setCompleted(TransactionStatus status) {
        if (!isCompleted()) {
            try (CloseableLock lock = new CloseableLock(accessLock)) {
                if (!isCompleted()) {
                    stopTimedRestoration();
                    transferMessage = null;
                    otherParticipants = null;
                    setStatus(status);
                }
            }
        }
    }

    private void setStatus(TransactionStatus status) {
        transactionStatus = status;
    }

    /**
     * A transaction is completed if it is either {@link TransactionStatus#COMMITTED} or
     * {@link TransactionStatus#ABORTED}.
     * 
     * @return
     */
    public boolean isCompleted() {
        return transactionStatus == TransactionStatus.COMMITTED
                || transactionStatus == TransactionStatus.ABORTED;
    }

    /**
     * Builder pattern for creating a {@link ReceivedTransactionContext} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private UUID transactionId;
        private TransactionStatus transactionStatus;
        private IKVTransferMessage transferMessage;
        private Set<ServerConnectionInfo> otherParticipants;
        private ITransactionRestorationRequest timedRestoration;

        public Builder transactionId(UUID transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder transactionStatus(TransactionStatus transactionStatus) {
            this.transactionStatus = transactionStatus;
            return this;
        }

        public Builder transferMessage(IKVTransferMessage transferMessage) {
            this.transferMessage = transferMessage;
            return this;
        }

        public Builder otherParticipants(Set<ServerConnectionInfo> otherParticipants) {
            this.otherParticipants = otherParticipants;
            return this;
        }

        public Builder timedRestoration(ITransactionRestorationRequest timedRestoration) {
            this.timedRestoration = timedRestoration;
            return this;
        }

        public ReceivedTransactionContext build() {
            return new ReceivedTransactionContext(this);
        }
    }

}
