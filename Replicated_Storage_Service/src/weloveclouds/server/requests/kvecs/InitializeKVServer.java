package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
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
    private Set<HashRange> readRanges;
    private HashRange writeRange;

    private Set<ServerConnectionInfo> replicaConnectionInfos;
    private ReplicationTransfererFactory replicationTransfererFactory;

    protected InitializeKVServer(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.ringMetadata = builder.ringMetadata;
        this.readRanges = builder.readRanges;
        this.writeRange = builder.writeRange;
        this.replicaConnectionInfos = builder.replicaConnectionInfos;
        this.replicationTransfererFactory = builder.replicationTransfererFactory;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing initialize KVServer request.");
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRanges(readRanges, writeRange);
        if (replicaConnectionInfos != null) {
            IReplicationTransferer replicationTransferer = replicationTransfererFactory
                    .createReplicationTransferer(replicaConnectionInfos);
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
            KVServerRequestsValidator.validateHashRanges(readRanges);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Read ranges are invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link InitializeKVServer} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IReplicableDataAccessService dataAccessService;
        private RingMetadata ringMetadata;
        private Set<HashRange> readRanges;
        private HashRange writeRange;
        private Set<ServerConnectionInfo> replicaConnectionInfos;
        private ReplicationTransfererFactory replicationTransfererFactory;

        /**
         * @param dataAccessService which is used for the data access
         */
        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        /**
         * @param ringMetadata metadata information about the server ring
         */
        public Builder ringMetadata(RingMetadata ringMetadata) {
            this.ringMetadata = ringMetadata;
            return this;
        }

        /**
         * @param readRanges {@link HashRange} ranges for which the server has READ privilege
         */
        public Builder readRanges(Set<HashRange> readRanges) {
            this.readRanges = readRanges;
            return this;
        }

        /**
         * @param readRanges {@link HashRange} range for which the server has WRITE privilege
         */
        public Builder writeRange(HashRange writeRange) {
            this.writeRange = writeRange;
            return this;
        }

        /**
         * @param replicaConnectionInfos connection information to the replica nodes
         */
        public Builder replicaConnectionInfos(Set<ServerConnectionInfo> replicaConnectionInfos) {
            this.replicaConnectionInfos = replicaConnectionInfos;
            return this;
        }

        /**
         * @param replicationTransfererFactory a factory to create replication transferer instances
         */
        public Builder replicationTransfererFactory(
                ReplicationTransfererFactory replicationTransfererFactory) {
            this.replicationTransfererFactory = replicationTransfererFactory;
            return this;
        }

        public InitializeKVServer build() {
            return new InitializeKVServer(this);
        }
    }
}
