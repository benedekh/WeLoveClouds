package weloveclouds.server.services.transaction;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.SetUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;
import weloveclouds.server.services.transaction.tasks.AbortTask;
import weloveclouds.server.services.transaction.tasks.CommitReadyTask;
import weloveclouds.server.services.transaction.tasks.CommitTask;
import weloveclouds.server.services.transaction.tasks.InitTask;

public class TransactionSenderService {

    private static final Logger LOGGER = Logger.getLogger(TransactionSenderService.class);

    private IConcurrentCommunicationApi communicationApi;
    private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
    private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;

    private ConnectionFactory connectionFactory;
    private ExecutorService executorService;

    public void executeTransactionsFor(AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> participantConnectionInfos) {
        executeTransactionsFor(replicationRequest.getTransferMessage(), participantConnectionInfos);
    }

    public void executeTransactionsFor(KVTransferMessage transferMessage,
            Set<ServerConnectionInfo> participantConnectionInfos) {
        try {
            Set<SenderTransaction> transactions = new HashSet<>();
            UUID transactionId = UUID.randomUUID();
            for (ServerConnectionInfo connectionInfo : participantConnectionInfos) {
                try {
                    Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                    Set<ServerConnectionInfo> otherParticipants =
                            SetUtils.removeValueFromSet(participantConnectionInfos, connectionInfo);

                    SenderTransaction senderTransaction = createTransactionFor(transferMessage,
                            transactionId, connection, otherParticipants);
                    transactions.add(senderTransaction);
                } catch (IOException ex) {
                    LOGGER.error(StringUtils.join("", "Cannot create connection to (",
                            connectionInfo, ") for transferring (", transferMessage, ")"));
                    return;
                }
            }
            executeTransactions(transactions);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    private void executeTransactions(Set<SenderTransaction> transactions) {
        try {
            executorService = Executors.newFixedThreadPool(transactions.size());
            AbortTask abortTask = new AbortTask(transactions, executorService);
            CommitTask commitTask = new CommitTask(abortTask, transactions, executorService);
            CommitReadyTask commitReadyTask =
                    new CommitReadyTask(commitTask, abortTask, transactions, executorService);
            InitTask initTask =
                    new InitTask(commitReadyTask, abortTask, transactions, executorService);
            initTask.execute();
        } finally {
            closeTransactions(transactions);
            executorService.shutdown();
        }
    }

    private void closeTransactions(Set<SenderTransaction> transactions) {
        for (SenderTransaction transaction : transactions) {
            transaction.close();
        }
    }

    private SenderTransaction createTransactionFor(KVTransferMessage transferMessage,
            UUID transactionId, Connection connection,
            Set<ServerConnectionInfo> otherParticipants) {
        return new SenderTransaction.Builder().connection(connection)
                .communicationApi(communicationApi).transactionId(transactionId)
                .transferMessage(transferMessage).otherParticipants(otherParticipants)
                .transactionMessageSerializer(transactionMessageSerializer)
                .transactionMessageDeserializer(transactionMessageDeserializer).build();
    }

}
