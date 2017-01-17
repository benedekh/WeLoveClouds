package weloveclouds.server.services.transaction.tasks;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;

/**
 * An abstract task that shall be executed for a transaction. If the task was successful, then the
 * follower task is the one referred by {@link #successorForSuccess}, otherwise the next task will
 * be {@link #successorForFail}.
 * 
 * @author Benedek
 * 
 * @param <E> type of the builder at runtime
 */
public abstract class TransactionTask<E extends TransactionTask.Builder<E>>
        implements ITransactionTask {

    private static final Logger LOGGER = Logger.getLogger(TransactionTask.class);
    private static final Duration MAX_WAITING_TIME_FOR_RESPONSE = new Duration(2 * 1000);

    private ITransactionTask successorForSuccess;
    private ITransactionTask successorForFail;

    protected Set<SenderTransaction> transactions;
    protected ExecutorService executorService;

    protected TransactionTask(Builder<E> builder) {
        this.successorForSuccess = builder.successorForSuccess;
        this.successorForFail = builder.successorForFail;
        this.transactions = builder.transactions;
        this.executorService = builder.executorService;
    }

    @Override
    public void execute() {
        try {
            executeTask();
            if (successorForSuccess != null) {
                successorForSuccess.execute();
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error(ex);
            if (successorForFail != null) {
                try {
                    successorForFail.execute();
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error(ex);
                }
            }
        }
    }

    protected long getMaxWaitingTimeInMillis() {
        return MAX_WAITING_TIME_FOR_RESPONSE.getMillis();
    }

    protected boolean everyonesHasTheExpectedStatus(Set<Future<StatusType>> statuses,
            StatusType status) throws InterruptedException, ExecutionException {
        for (Future<StatusType> response : statuses) {
            try {
                if (response.get(getMaxWaitingTimeInMillis(), TimeUnit.MILLISECONDS) != status) {
                    return false;
                }
            } catch (TimeoutException ex) {
                LOGGER.error(ex);
                return false;
            }
        }
        return true;
    }

    protected abstract void executeTask() throws InterruptedException, ExecutionException;

    /**
     * Builder pattern for creating a {@link TransactionTask} instance.
     *
     * @author Benedek
     */
    public abstract static class Builder<E extends Builder<E>> {
        private ITransactionTask successorForSuccess;
        private ITransactionTask successorForFail;
        private Set<SenderTransaction> transactions;
        private ExecutorService executorService;

        @SuppressWarnings("unchecked")
        public E successorForSuccess(ITransactionTask successorForSuccess) {
            this.successorForSuccess = successorForSuccess;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E successorForFail(ITransactionTask successorForFail) {
            this.successorForFail = successorForFail;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E transactions(Set<SenderTransaction> transactions) {
            this.transactions = transactions;
            return (E) this;
        }

        @SuppressWarnings("unchecked")
        public E executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return (E) this;
        }

    }
}
