package weloveclouds.server.requests.kvecs;

import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.datastore.IReplicableDataAccessService;

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

    protected InitializeKVServer(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.ringMetadata = builder.ringMetadata;
        this.readRanges = builder.readRanges;
        this.writeRange = builder.writeRange;
        this.replicaConnectionInfos = builder.replicaConnectionInfos;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing initialize KVServer request.");
        if (!dataAccessService.isServiceInitialized()) {
            dataAccessService.setRingMetadata(ringMetadata);
            dataAccessService.setManagedHashRanges(readRanges, writeRange);
            dataAccessService.setReplicaConnectionInfos(replicaConnectionInfos);
            LOGGER.debug("Initialization finished successfully.");
            return createSuccessKVAdminMessage();
        } else {
            return createErrorKVAdminMessage("Data access service is already initialized.");
        }
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
            LOGGER.error(ringMetadata);
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

        public InitializeKVServer build() {
            return new InitializeKVServer(this);
        }
    }
}
