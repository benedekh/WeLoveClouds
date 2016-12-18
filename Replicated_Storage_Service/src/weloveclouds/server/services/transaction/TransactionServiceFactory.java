package weloveclouds.server.services.transaction;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import weloveclouds.commons.kvstore.deserialization.KVTransactionMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.serialization.KVTransactionMessageSerializer;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.server.requests.kvserver.transaction.IKVTransactionRequest;
import weloveclouds.server.requests.kvserver.transaction.TransactionRecieverService;
import weloveclouds.server.requests.kvserver.transaction.utils.TimedAbortRequest;
import weloveclouds.server.requests.kvserver.transaction.utils.TransactionStatus;
import weloveclouds.server.requests.kvserver.transfer.KVTransferRequestFactory;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.SimulatedMovableDataAccessService;
import weloveclouds.server.services.transaction.flow.TwoPCExecutionFlow;

public class TransactionServiceFactory {

    public ITransactionSenderService create2PCTransactionSenderService() {
        return new TransactionSenderService.Builder()
                .communicationApi(
                        new CommunicationApiFactory().createConcurrentCommunicationApiV1())
                .connectionFactory(new ConnectionFactory(new SocketFactory()))
                .transactionExecutor(new TwoPCExecutionFlow())
                .transactionMessageDeserializer(new KVTransactionMessageDeserializer())
                .transactionMessageSerializer(new KVTransactionMessageSerializer()).build();
    }

    public IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> createTransactionRecieverService(
            IMovableDataAccessService dataAccessService) {
        SimulatedMovableDataAccessService simulatedDAS = new SimulatedMovableDataAccessService();
        return new TransactionRecieverService.Builder()
                .ongoingTransactions(new ConcurrentHashMap<UUID, IKVTransferMessage>())
                .timedAbortRequests(new ConcurrentHashMap<UUID, TimedAbortRequest>())
                .transactionLog(new ConcurrentHashMap<UUID, TransactionStatus>())
                .simulatedDASBehavior(new KVTransferRequestFactory(simulatedDAS))
                .realDASBehavior(new KVTransferRequestFactory(dataAccessService)).build();
    }

}
