package weloveclouds.server.services.transaction;

import weloveclouds.commons.kvstore.deserialization.KVTransactionMessageDeserializer;
import weloveclouds.commons.kvstore.serialization.KVTransactionMessageSerializer;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.models.ConnectionFactory;
import weloveclouds.server.services.transaction.flow.TwoPCExecutionFlow;

public class TransactionSenderServiceFactory {

    public ITransactionSenderService create2PCTransactionSenderService() {
        return new TransactionSenderService.Builder()
                .communicationApi(
                        new CommunicationApiFactory().createConcurrentCommunicationApiV1())
                .connectionFactory(new ConnectionFactory(new SocketFactory()))
                .transactionExecutor(new TwoPCExecutionFlow())
                .transactionMessageDeserializer(new KVTransactionMessageDeserializer())
                .transactionMessageSerializer(new KVTransactionMessageSerializer()).build();
    }

}
