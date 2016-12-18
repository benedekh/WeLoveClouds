package weloveclouds.server.services.transaction;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;
import weloveclouds.server.services.transaction.flow.ITransactionExecutionFlow;

public class TransactionSenderService implements ITransactionSenderService {

    private static final Logger LOGGER = Logger.getLogger(TransactionSenderService.class);

    private IConcurrentCommunicationApi communicationApi;
    private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
    private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;

    private ConnectionFactory connectionFactory;
    private ITransactionExecutionFlow transactionExecutor;

    protected TransactionSenderService(Builder builder) {
        this.communicationApi = builder.communicationApi;
        this.transactionMessageSerializer = builder.transactionMessageSerializer;
        this.transactionMessageDeserializer = builder.transactionMessageDeserializer;
        this.connectionFactory = builder.connectionFactory;
        this.transactionExecutor = builder.transactionExecutor;
    }

    @Override
    public void executeTransactionsFor(AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> participantConnectionInfos) {
        executeTransactionsFor(replicationRequest.getTransferMessage(), participantConnectionInfos);
    }

    @Override
    public void executeTransactionsFor(KVTransferMessage transferMessage,
            Set<ServerConnectionInfo> participantConnectionInfos) {
        try {
            Set<SenderTransaction> transactions = new HashSet<>();
            UUID transactionId = UUID.randomUUID();
            for (ServerConnectionInfo connectionInfo : participantConnectionInfos) {
                try {
                    Connection connection = connectionFactory.createConnectionFrom(connectionInfo);
                    SenderTransaction senderTransaction = new SenderTransaction.Builder()
                            .connection(connection).communicationApi(communicationApi)
                            .transactionId(transactionId).transferMessage(transferMessage)
                            .transactionMessageSerializer(transactionMessageSerializer)
                            .transactionMessageDeserializer(transactionMessageDeserializer).build();
                    transactions.add(senderTransaction);
                } catch (IOException ex) {
                    LOGGER.error(StringUtils.join("", "Cannot create connection to (",
                            connectionInfo, ") for transferring (", transferMessage, ")"));
                    return;
                }
            }
            transactionExecutor.executeTransactions(transactions);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public static class Builder {
        private IConcurrentCommunicationApi communicationApi;
        private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
        private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;
        private ConnectionFactory connectionFactory;
        private ITransactionExecutionFlow transactionExecutor;

        public Builder communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder connectionFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public Builder transactionExecutor(ITransactionExecutionFlow transactionExecutor) {
            this.transactionExecutor = transactionExecutor;
            return this;
        }

        public Builder transactionMessageSerializer(
                IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer) {
            this.transactionMessageSerializer = transactionMessageSerializer;
            return this;
        }

        public Builder transactionMessageDeserializer(
                IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer) {
            this.transactionMessageDeserializer = transactionMessageDeserializer;
            return this;
        }

        public ITransactionSenderService build() {
            return new TransactionSenderService(this);
        }
    }

}
