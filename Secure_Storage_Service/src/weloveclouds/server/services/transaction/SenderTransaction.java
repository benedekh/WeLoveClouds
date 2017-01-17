package weloveclouds.server.services.transaction;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransactionMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Represents a transaction on the sender side.
 * 
 * @author Benedek
 */
public class SenderTransaction implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(SenderTransaction.class);

    private UUID transactionId;
    private IKVTransferMessage transferMessage;
    private Set<ServerConnectionInfo> otherParticipants;

    private IConcurrentCommunicationApi communicationApi;
    private Connection<?> connection;

    private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
    private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;

    protected SenderTransaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.transferMessage = builder.transferMessage;
        this.otherParticipants = builder.otherParticipants;
        this.communicationApi = builder.communicationApi;
        this.connection = builder.connection;
        this.transactionMessageSerializer = builder.transactionMessageSerializer;
        this.transactionMessageDeserializer = builder.transactionMessageDeserializer;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ex) {
                LOGGER.error(ex);
            }
        }
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    private StatusType sendAndExpectForResponse(KVTransactionMessage message)
            throws IOException, DeserializationException {
        LOGGER.debug(StringUtils.join(" ", "Sending transaction status" + message.getStatus(), "to",
                connection));
        byte[] serialized = transactionMessageSerializer.serialize(message).getBytes();
        byte[] response = communicationApi.sendAndExpectForResponse(serialized, connection);
        IKVTransactionMessage responseMessage =
                transactionMessageDeserializer.deserialize(response);
        return responseMessage.getStatus();
    }

    private KVTransactionMessage createTransactionMessage(StatusType status) {
        return createTransactionMessage(status, null, null);
    }

    private KVTransactionMessage createTransactionMessage(StatusType status,
            IKVTransferMessage transferMessage, Set<ServerConnectionInfo> otherParticipants) {
        return new KVTransactionMessage.Builder().status(status).transactionId(transactionId)
                .transferPayload(transferMessage).otherParticipants(otherParticipants).build();
    }

    /**
     * Sends the INIT phase for the transaction.
     * 
     * @author Benedek
     */
    public class Init implements Callable<StatusType> {
        @Override
        public StatusType call() throws Exception {
            KVTransactionMessage message =
                    createTransactionMessage(StatusType.INIT, transferMessage, otherParticipants);
            return sendAndExpectForResponse(message);
        }
    }

    /**
     * Sends the COMMIT READY phase for the transaction.
     * 
     * @author Benedek
     */
    public class CommitReady implements Callable<StatusType> {
        @Override
        public StatusType call() throws Exception {
            KVTransactionMessage message = createTransactionMessage(StatusType.COMMIT_READY);
            return sendAndExpectForResponse(message);
        }
    }

    /**
     * Sends the COMMIT phase for the transaction.
     * 
     * @author Benedek
     */
    public class Commit implements Callable<StatusType> {
        @Override
        public StatusType call() throws Exception {
            KVTransactionMessage message = createTransactionMessage(StatusType.COMMIT);
            return sendAndExpectForResponse(message);
        }
    }

    /**
     * Sends the ABORT phase for the transaction.
     * 
     * @author Benedek
     */
    public class Abort implements Callable<StatusType> {
        @Override
        public StatusType call() throws Exception {
            KVTransactionMessage message = createTransactionMessage(StatusType.ABORT);
            return sendAndExpectForResponse(message);
        }
    }

    /**
     * Sends the HELP (restoration) phase for the transaction.
     * 
     * @author Benedek
     */
    public class Help implements Callable<StatusType> {
        @Override
        public StatusType call() throws Exception {
            KVTransactionMessage message = createTransactionMessage(StatusType.HELP);
            return sendAndExpectForResponse(message);
        }
    }

    /**
     * Builder pattern for creating a {@link SenderTransaction} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private UUID transactionId;
        private IKVTransferMessage transferMessage;
        private Set<ServerConnectionInfo> otherParticipants;
        private IConcurrentCommunicationApi communicationApi;
        private Connection<?> connection;
        private IMessageSerializer<SerializedMessage, IKVTransactionMessage> transactionMessageSerializer;
        private IMessageDeserializer<IKVTransactionMessage, SerializedMessage> transactionMessageDeserializer;

        public Builder transactionId(UUID transactionId) {
            this.transactionId = transactionId;
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

        public Builder communicationApi(IConcurrentCommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        public Builder connection(Connection<?> connection) {
            this.connection = connection;
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

        public SenderTransaction build() {
            return new SenderTransaction(this);
        }

    }

}
