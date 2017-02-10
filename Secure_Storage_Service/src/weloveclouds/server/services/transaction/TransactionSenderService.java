package weloveclouds.server.services.transaction;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.SetUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.models.factory.AbstractConnectionFactory;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;
import weloveclouds.server.services.transaction.flow.ITransactionExecutionFlow;

/**
 * Service which sends transactions to each participant and executes them there.
 * 
 * @author Benedek
 */
public class TransactionSenderService implements ITransactionSenderService {

    private static final Logger LOGGER = Logger.getLogger(TransactionSenderService.class);

    private IConcurrentCommunicationApi communicationApi;
    private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
    private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;

    private AbstractConnectionFactory connectionFactory;
    private ITransactionExecutionFlow transactionExecutionFlow;

    protected TransactionSenderService(Builder builder) {
        this.communicationApi = builder.communicationApi;
        this.transactionMessageSerializer = builder.transactionMessageSerializer;
        this.transactionMessageDeserializer = builder.transactionMessageDeserializer;
        this.connectionFactory = builder.connectionFactory;
        this.transactionExecutionFlow = builder.transactionExecutionFlow;
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
                    Connection<?> connection =
                            connectionFactory.createConnectionFrom(connectionInfo);
                    Set<ServerConnectionInfo> otherParticipants =
                            SetUtils.removeValueFromSet(participantConnectionInfos, connectionInfo);
                    transactions.add(createTransaction(connection, transactionId, transferMessage,
                            otherParticipants));
                } catch (IOException ex) {
                    LOGGER.error(StringUtils.join("", "Cannot create connection to (",
                            connectionInfo, ") for transferring (", transferMessage, ")"));
                }
            }
            transactionExecutionFlow.executeTransactions(transactions);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public void executeTransactionReferredByIDFor(UUID transactionId,
            Set<ServerConnectionInfo> participantConnectionInfos) {
        try {
            Set<SenderTransaction> transactions = new HashSet<>();
            for (ServerConnectionInfo connectionInfo : participantConnectionInfos) {
                try {
                    Connection<?> connection =
                            connectionFactory.createConnectionFrom(connectionInfo);
                    transactions.add(createTransaction(connection, transactionId, null, null));
                } catch (IOException ex) {
                    LOGGER.error(ex);
                    return;
                }
            }
            transactionExecutionFlow.executeTransactions(transactions);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    private SenderTransaction createTransaction(Connection<?> connection, UUID transactionId,
            IKVTransferMessage transferMessage, Set<ServerConnectionInfo> otherParticipants) {
        return new SenderTransaction.Builder().connection(connection)
                .communicationApi(communicationApi).transactionId(transactionId)
                .transferMessage(transferMessage).otherParticipants(otherParticipants)
                .transactionMessageSerializer(transactionMessageSerializer)
                .transactionMessageDeserializer(transactionMessageDeserializer).build();
    }

    /**
     * Builder pattern for creating a {@link TransactionSenderService} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IConcurrentCommunicationApi communicationApi;
        private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
        private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;
        private AbstractConnectionFactory connectionFactory;
        private ITransactionExecutionFlow transactionExecutionFlow;

        public Builder communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder connectionFactory(AbstractConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public Builder transactionExecutionFlow(ITransactionExecutionFlow executionFlow) {
            this.transactionExecutionFlow = executionFlow;
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
