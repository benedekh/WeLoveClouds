package weloveclouds.server.services.transaction.flow.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.requests.kvserver.transaction.AbortRequest;
import weloveclouds.server.requests.kvserver.transaction.CommitRequest;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.flow.TransactionExecutionFlow;
import weloveclouds.server.services.transaction.tasks.ITransactionTask;
import weloveclouds.server.services.transaction.tasks.twopc.restore.CommitOrAbortDecisionTask;
import weloveclouds.server.services.transaction.tasks.twopc.restore.SendHelpTask;

/**
 * Steps in the restoration phase of a two-phase-commit protocol to be executed.
 * 
 * @author Benedek
 */
public class TwoPCReceiverSideRestorationFlow extends TransactionExecutionFlow {

    private AbortRequest abortRequest;
    private CommitRequest commitRequest;

    public TwoPCReceiverSideRestorationFlow(AbortRequest abortRequest,
            CommitRequest commitRequest) {
        this.abortRequest = abortRequest;
        this.commitRequest = commitRequest;
    }

    @Override
    public void executeFlowForTransactions(Set<SenderTransaction> transactions) throws Exception {
        Set<Future<StatusType>> setForResponses = new HashSet<>();
        ITransactionTask decisionTask =
                new CommitOrAbortDecisionTask.Builder().abortRequest(abortRequest)
                        .commitRequest(commitRequest).responseFutures(setForResponses).build();
        ITransactionTask sendHelpTask = new SendHelpTask.Builder().executorService(executorService)
                .setForResponses(setForResponses).transactions(transactions)
                .successorForSuccess(decisionTask).successorForFail(decisionTask).build();
        sendHelpTask.execute();
    }


}
