package weloveclouds.server.services.transaction;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import weloveclouds.commons.kvstore.deserialization.KVTransactionMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.serialization.KVTransactionMessageSerializer;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.server.requests.kvserver.transaction.AbortRequest;
import weloveclouds.server.requests.kvserver.transaction.CommitRequest;
import weloveclouds.server.requests.kvserver.transaction.IKVTransactionRequest;
import weloveclouds.server.requests.kvserver.transaction.TransactionReceiverService;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transfer.KVTransferRequestFactory;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.datastore.SimulatedMovableDataAccessService;
import weloveclouds.server.services.transaction.flow.ITransactionExecutionFlow;
import weloveclouds.server.services.transaction.flow.twopc.TwoPCCoordinatorExecutionFlow;
import weloveclouds.server.services.transaction.flow.twopc.TwoPCReceiverSideRestorationFlow;

public class TransactionServiceFactory {

    public ITransactionSenderService create2PCReceiverSideRestorationService(
            AbortRequest abortRequest, CommitRequest commitRequest) {
        return createTransactionSenderService(
                new TwoPCReceiverSideRestorationFlow(abortRequest, commitRequest));
    }

    public ITransactionSenderService create2PCTransactionSenderService() {
        return createTransactionSenderService(new TwoPCCoordinatorExecutionFlow());
    }

    public IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> createTransactionReceiverService(
            IMovableDataAccessService dataAccessService) {
        SimulatedMovableDataAccessService simulatedDAS = new SimulatedMovableDataAccessService();
        return new TransactionReceiverService.Builder()
                .transactionLog(new ConcurrentHashMap<UUID, ReceivedTransactionContext>())
                .transactionServiceFactory(this)
                .simulatedDASBehavior(new KVTransferRequestFactory(simulatedDAS))
                .realDASBehavior(new KVTransferRequestFactory(dataAccessService)).build();
    }

    private ITransactionSenderService createTransactionSenderService(
            ITransactionExecutionFlow executionFlow) {
        return new TransactionSenderService.Builder()
                .communicationApi(
                        new CommunicationApiFactory().createConcurrentCommunicationApiV1())
                .connectionFactory(new ConnectionFactory(new SocketFactory()))
                .transactionExecutionFlow(executionFlow)
                .transactionMessageDeserializer(new KVTransactionMessageDeserializer())
                .transactionMessageSerializer(new KVTransactionMessageSerializer()).build();
    }


}
