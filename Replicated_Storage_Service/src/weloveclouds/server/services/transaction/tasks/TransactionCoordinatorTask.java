package weloveclouds.server.services.transaction.tasks;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;

public abstract class TransactionCoordinatorTask<E extends TransactionCoordinatorTask.Builder<E>>
        implements ITransactionTask {

    private static final Logger LOGGER = Logger.getLogger(TransactionCoordinatorTask.class);

    private ITransactionTask successorForSuccess;
    private ITransactionTask successorForFail;

    protected Set<SenderTransaction> transactions;
    protected ExecutorService executorService;

    protected TransactionCoordinatorTask(Builder<E> builder) {
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

    protected boolean everyonesHasTheExpectedStatus(Set<Future<StatusType>> statuses,
            StatusType status) throws InterruptedException, ExecutionException {
        for (Future<StatusType> response : statuses) {
            if (response.get() != status) {
                return false;
            }
        }
        return true;
    }

    protected abstract void executeTask() throws InterruptedException, ExecutionException;

    public abstract static class Builder<E extends Builder<E>> {
        private ITransactionTask successorForSuccess;
        private ITransactionTask successorForFail;
        private Set<SenderTransaction> transactions;
        private ExecutorService executorService;

        public Builder<E> successorForSuccess(ITransactionTask successorForSuccess) {
            this.successorForSuccess = successorForSuccess;
            return getThis();
        }

        public Builder<E> successorForFail(ITransactionTask successorForFail) {
            this.successorForFail = successorForFail;
            return getThis();
        }

        public Builder<E> transactions(Set<SenderTransaction> transactions) {
            this.transactions = transactions;
            return getThis();
        }

        public Builder<E> executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return getThis();
        }


        protected abstract E getThis();

        public abstract ITransactionTask build();

    }
}
