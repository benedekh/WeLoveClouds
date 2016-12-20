package weloveclouds.server.services.transaction.flow.twopc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.services.transaction.SenderTransaction;
import weloveclouds.server.services.transaction.flow.TransactionExecutionFlow;
import weloveclouds.server.services.transaction.tasks.ITransactionTask;
import weloveclouds.server.services.transaction.tasks.twopc.restore.CommitOrAbortDecisionTask;
import weloveclouds.server.services.transaction.tasks.twopc.restore.SendHelpTask;

public class TwoPCReceiverSideRestorationFlow extends TransactionExecutionFlow {

    private ReceivedTransactionContext transactionContext;

    public TwoPCReceiverSideRestorationFlow(ReceivedTransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    @Override
    public void executeFlowForTransactions(Set<SenderTransaction> transactions) throws Exception {
        Set<Future<StatusType>> setForResponses = new HashSet<>();
        ITransactionTask decisionTask =
                new CommitOrAbortDecisionTask(transactionContext, setForResponses);
        ITransactionTask sendHelpTask = new SendHelpTask.Builder().executorService(executorService)
                .setForResponses(setForResponses).transactions(transactions)
                .successorForSuccess(decisionTask).successorForFail(decisionTask).build();
        sendHelpTask.execute();
    }


}
