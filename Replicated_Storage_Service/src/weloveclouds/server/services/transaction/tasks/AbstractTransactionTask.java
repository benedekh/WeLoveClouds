package weloveclouds.server.services.transaction.tasks;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.model.TransactionWithResponseStatus;

public abstract class AbstractTransactionTask implements ITransactionTask {

    private static final Logger LOGGER = Logger.getLogger(AbstractTransactionTask.class);

    private ITransactionTask successorForSuccess;
    private ITransactionTask successorForFail;

    protected Set<SenderTransaction> transactions;
    protected ExecutorService executorService;

    public AbstractTransactionTask(ITransactionTask successorForSuccess,
            ITransactionTask successorForFail, Set<SenderTransaction> transactions,
            ExecutorService executorService) {
        this.successorForSuccess = successorForSuccess;
        this.successorForFail = successorForFail;
        this.transactions = transactions;
        this.executorService = executorService;
    }

    public AbstractTransactionTask(ITransactionTask successorForFail,
            Set<SenderTransaction> transactions, ExecutorService executorService) {
        this.successorForFail = successorForFail;
        this.transactions = transactions;
        this.executorService = executorService;
    }


    public AbstractTransactionTask(Set<SenderTransaction> transactions,
            ExecutorService executorService) {
        this.transactions = transactions;
        this.executorService = executorService;
    }

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

    protected abstract void executeTask() throws InterruptedException, ExecutionException;

    protected boolean everyonesHasTheExpectedStatus(
            Set<Future<TransactionWithResponseStatus>> statuses, StatusType status)
            throws InterruptedException, ExecutionException {
        for (Future<TransactionWithResponseStatus> response : statuses) {
            if (response.get().getResponseStatus() != status) {
                return false;
            }
        }
        return true;
    }
}
