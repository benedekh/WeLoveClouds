package weloveclouds.server.services.replication;

import weloveclouds.commons.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.server.services.replication.request.ReplicationRequestFactory;
import weloveclouds.server.services.transaction.ITransactionSenderService;
import weloveclouds.server.services.transaction.TransactionServiceFactory;

/**
 * Factory to create {@link ReplicationService} instances.
 * 
 * @author Benedek
 */
public class ReplicationServiceFactory {

    /**
     * @param transactionSenderService the transaction protocol to be used by the replication
     *        service
     * @return a {@link ReplicationService} instances which uses the referred transaction protocol
     */
    public ReplicationService createReplicationService(
            ITransactionSenderService transactionSenderService) {
        return new ReplicationService.Builder().transactionSenderService(transactionSenderService)
                .statefulReplicationFactory(
                        new ReplicationRequestFactory(new KVTransferMessageSerializer()))
                .build();
    }

    /**
     * A {@link ReplicationService} instances which uses 2-phase-commit protocol for transactions.
     */
    public ReplicationService createReplicationServiceWith2PC() {
        return createReplicationService(
                new TransactionServiceFactory().create2PCTransactionSenderService());
    }

}
