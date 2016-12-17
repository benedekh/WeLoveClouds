package weloveclouds.server.services.transaction;

import java.util.Set;

import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;

public interface ITransactionSenderService {

    void executeTransactionsFor(AbstractReplicationRequest<?, ?> replicationRequest,
            Set<ServerConnectionInfo> participantConnectionInfos);

    void executeTransactionsFor(KVTransferMessage transferMessage,
            Set<ServerConnectionInfo> participantConnectionInfos);

}
