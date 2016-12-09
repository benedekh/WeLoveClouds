package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IReplicableDataAccessService;
import weloveclouds.server.services.utils.IReplicationTransferer;
import weloveclouds.server.services.utils.ReplicationTransfererFactory;

/**
 * An initialization request to the {@link IReplicableDataAccessService}, which initializes the
 * service.
 * 
 * @author Benedek
 */
public class InitializeKVServer implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(InitializeKVServer.class);

    private IReplicableDataAccessService dataAccessService;
    private RingMetadata ringMetadata;
    private HashRangesWithRoles rangesManagedByServer;

    private ServerConnectionInfos replicaConnectionInfos;
    private ReplicationTransfererFactory replicationTransfererFactory;

    /**
     * @param dataAccessService which is used for the data access
     * @param replicationTransfererFactory a factory to create replication transferer instances
     * @param ringMetadata metadata information about the server ring
     * @param rangesManagedByServer the hash ranges which are managed by this server together with
     *        roles the server has for each range
     * @param replicaConnectionInfos connection information to the replica nodes
     */
    public InitializeKVServer(IReplicableDataAccessService dataAccessService,
            ReplicationTransfererFactory replicationTransfererFactory, RingMetadata ringMetadata,
            HashRangesWithRoles rangesManagedByServer,
            ServerConnectionInfos replicaConnectionInfos) {
        this.dataAccessService = dataAccessService;
        this.ringMetadata = ringMetadata;
        this.rangesManagedByServer = rangesManagedByServer;
        this.replicaConnectionInfos = replicaConnectionInfos;
        this.replicationTransfererFactory = replicationTransfererFactory;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing initialize KVServer request.");
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRanges(rangesManagedByServer);
        if (replicaConnectionInfos != null) {
            IReplicationTransferer replicationTransferer = replicationTransfererFactory
                    .createReplicationTransferer(replicaConnectionInfos.getServerConnectionInfos());
            dataAccessService.setReplicationTransferer(replicationTransferer);
        }
        LOGGER.debug("Initialization finished successfully.");
        return createSuccessKVAdminMessage();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        try {
            if (replicaConnectionInfos != null) {
                KVServerRequestsValidator.validateServerConnectionInfos(replicaConnectionInfos);
            }
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Replica connection infos are invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        try {
            KVServerRequestsValidator.validateRingMetadata(ringMetadata);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Ring metadata is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        try {
            KVServerRequestsValidator.validateHashRangesWithRoles(rangesManagedByServer);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Hash range with roles is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

}
