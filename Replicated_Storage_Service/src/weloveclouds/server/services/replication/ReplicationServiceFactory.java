package weloveclouds.server.services.replication;

import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.server.services.replication.request.ReplicationRequestFactory;
import weloveclouds.server.services.transaction.ITransactionSenderService;
import weloveclouds.server.services.transaction.TransactionSenderServiceFactory;

public class ReplicationServiceFactory {

    public ReplicationService createReplicationService(
            ITransactionSenderService transactionSenderService) {
        return new ReplicationService.Builder().transactionSenderService(transactionSenderService)
                .statefulReplicationFactory(
                        new ReplicationRequestFactory(new KVTransferMessageSerializer()))
                .build();
    }

    public ReplicationService createReplicationServiceWith2PC() {
        return createReplicationService(
                new TransactionSenderServiceFactory().create2PCTransactionSenderService());
    }

}
