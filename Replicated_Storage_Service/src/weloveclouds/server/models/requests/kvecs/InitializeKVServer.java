package weloveclouds.server.models.requests.kvecs;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.replication.HashRangesWithRoles;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * An initialization request to the {@link IMovableDataAccessService}, which initializes the
 * service.
 * 
 * @author Benedek
 */
public class InitializeKVServer implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(InitializeKVServer.class);

    private IMovableDataAccessService dataAccessService;
    private RingMetadata ringMetadata;
    private HashRangesWithRoles rangesManagedByServer;

    /**
     * @param dataAccessService which is used for the data access
     * @param ringMetadata metadata information about the server ring
     * @param rangesManagedByServer the hash ranges which are managed by this server together with
     *        roles the server has for each range
     */
    public InitializeKVServer(IMovableDataAccessService dataAccessService,
            RingMetadata ringMetadata, HashRangesWithRoles rangesManagedByServer) {
        this.dataAccessService = dataAccessService;
        this.ringMetadata = ringMetadata;
        this.rangesManagedByServer = rangesManagedByServer;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing initialize KVServer request.");
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRanges(rangesManagedByServer);
        LOGGER.debug("Initialization finished successfully.");
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
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

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
    }

}
