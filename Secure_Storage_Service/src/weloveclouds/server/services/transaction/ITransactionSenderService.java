package weloveclouds.server.services.transaction;

import java.util.Set;
import java.util.UUID;

import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;

/**
 * Service which sends transactions to each participant and executes them there.
 * 
 * @author Benedek
 */
public interface ITransactionSenderService {

    /**
     * Executes the respective {@link replicationRequest} on each participant denoted by their
     * connection info.
     */
    public void executeTransactionsFor(AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> participantConnectionInfos);

    /**
     * Executes the respective {@link transferMessage} on each participant denoted by their
     * connection info.
     */
    public void executeTransactionsFor(KVTransferMessage transferMessage,
            Set<ServerConnectionInfo> participantConnectionInfos);

    /**
     * Executes the respective transaction denoted by its ID ({@link transactionId}) on each
     * participant denoted by their connection info.
     */
    public void executeTransactionReferredByIDFor(UUID transactionId,
            Set<ServerConnectionInfo> participantConnectionInfos);

}
