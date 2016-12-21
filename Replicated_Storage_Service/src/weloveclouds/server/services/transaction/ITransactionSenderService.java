package weloveclouds.server.services.transaction;

import java.util.Set;
import java.util.UUID;

import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;

public interface ITransactionSenderService {

    public void executeTransactionsFor(AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> participantConnectionInfos);

    public void executeTransactionsFor(KVTransferMessage transferMessage,
            Set<ServerConnectionInfo> participantConnectionInfos);

    public void executeEmptyTransactionsFor(UUID transactionId,
            Set<ServerConnectionInfo> participantConnectionInfos);

}
