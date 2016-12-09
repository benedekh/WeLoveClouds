package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IReplicableDataAccessService;
import weloveclouds.server.services.utils.IReplicationTransferer;
import weloveclouds.server.services.utils.ReplicationTransfererFactory;

/**
 * An update metadata request to the {@link IReplicableDataAccessService}, which defines in what
 * range shall be the keys of the entries which are stored on this server.
 *
 * @author Benedek
 */
public class UpdateRingMetadata implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(UpdateRingMetadata.class);

    private IReplicableDataAccessService dataAccessService;
    private RingMetadata ringMetadata;
    private HashRangesWithRoles rangesManagedByServer;

    private ServerConnectionInfos replicaConnectionInfos;
    private ReplicationTransfererFactory replicationTransfererFactory;

    /**
     * @param dataAccessService a reference to the data access service
     * @param ringMetadata the metadata information about the ring in which the servers are placed
     * @param rangesManagedByServer the hash ranges which are managed by this server together with
     *        roles the server has for each range
     */
    public UpdateRingMetadata(IReplicableDataAccessService dataAccessService,
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
        LOGGER.debug("Executing update ring metadata request.");
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRanges(rangesManagedByServer);

        IReplicationTransferer replicationTransferer = null;
        if (replicaConnectionInfos != null) {
            replicationTransferer = replicationTransfererFactory
                    .createReplicationTransferer(replicaConnectionInfos.getServerConnectionInfos());
        }
        dataAccessService.setReplicationTransferer(replicationTransferer);

        LOGGER.debug("Update ring metadata request finished successfully.");
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
