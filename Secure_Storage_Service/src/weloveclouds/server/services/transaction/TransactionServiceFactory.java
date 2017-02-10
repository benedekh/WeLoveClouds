package weloveclouds.server.services.transaction;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import weloveclouds.commons.kvstore.deserialization.KVTransactionMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.serialization.KVTransactionMessageSerializer;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.networking.socket.client.SSLSocketFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.factory.SecureConnectionFactory;
import weloveclouds.server.requests.kvserver.transaction.AbortRequest;
import weloveclouds.server.requests.kvserver.transaction.CommitRequest;
import weloveclouds.server.requests.kvserver.transaction.IKVTransactionRequest;
import weloveclouds.server.requests.kvserver.transaction.TransactionReceiverService;
import weloveclouds.server.requests.kvserver.transaction.models.ReceivedTransactionContext;
import weloveclouds.server.requests.kvserver.transfer.KVTransferRequestFactory;
import weloveclouds.server.services.datastore.IMovableDataAccessService;
import weloveclouds.server.services.transaction.flow.ITransactionExecutionFlow;
import weloveclouds.server.services.transaction.flow.twopc.TwoPCCoordinatorExecutionFlow;
import weloveclouds.server.services.transaction.flow.twopc.TwoPCReceiverSideRestorationFlow;

/**
 * Factory for creating transaction services.
 * 
 * @author Benedek
 */
public class TransactionServiceFactory {

    /**
     * Creates a 2PC transaction sender service for receiver side's restoration. (In order to send
     * help messages to other participants and decide the status of the transaction, based on their
     * responses.)
     * 
     * @param abortRequest the request to be processed in case everyone voted for abort
     * @param commitRequest the request to be processed in case everyone voted for commit
     */
    public ITransactionSenderService create2PCReceiverSideRestorationService(
            AbortRequest abortRequest, CommitRequest commitRequest) {
        return createTransactionSenderService(
                new TwoPCReceiverSideRestorationFlow(abortRequest, commitRequest));
    }

    /**
     * Creates a transaction sender service with two-phase-commit.
     */
    public ITransactionSenderService create2PCTransactionSenderService() {
        return createTransactionSenderService(new TwoPCCoordinatorExecutionFlow());
    }

    /**
     * Creates a transaction receiver service.
     * 
     * @param dataAccessService the data access service which will store the effects of the
     *        transactions
     */
    public IRequestFactory<IKVTransactionMessage, IKVTransactionRequest> createTransactionReceiverService(
            IMovableDataAccessService dataAccessService) {
        return new TransactionReceiverService.Builder()
                .transactionLog(new ConcurrentHashMap<UUID, ReceivedTransactionContext>())
                .transactionServiceFactory(this)
                .simulatedDASBehavior(new KVTransferRequestFactory(
                        dataAccessService.getSimulatedDataAccessService()))
                .realDASBehavior(new KVTransferRequestFactory(dataAccessService)).build();
    }

    private ITransactionSenderService createTransactionSenderService(
            ITransactionExecutionFlow executionFlow) {
        return new TransactionSenderService.Builder()
                .communicationApi(
                        new CommunicationApiFactory().createConcurrentCommunicationApiV1())
                .connectionFactory(new SecureConnectionFactory(new SSLSocketFactory()))
                .transactionExecutionFlow(executionFlow)
                .transactionMessageDeserializer(new KVTransactionMessageDeserializer())
                .transactionMessageSerializer(new KVTransactionMessageSerializer()).build();
    }


}
