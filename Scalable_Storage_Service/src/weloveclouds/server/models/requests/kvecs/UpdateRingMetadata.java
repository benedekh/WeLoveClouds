package weloveclouds.server.models.requests.kvecs;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.networking.exceptions.IllegalRequestException;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * An update metadata request to the {@link IMovableDataAccessService}, which defines in what range
 * shall be the keys of the entries which are stored on this server.
 *
 * @author Benedek
 */
public class UpdateRingMetadata implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(UpdateRingMetadata.class);

    private IMovableDataAccessService dataAccessService;
    private RingMetadata ringMetadata;
    private HashRange rangeManagedByServer;

    /**
     * @param dataAccessService a reference to the data access service
     * @param ringMetadata the metadata information about the ring in which the servers are placed
     * @param rangeManagedByServer the range of hashes which are managed by the server
     */
    public UpdateRingMetadata(IMovableDataAccessService dataAccessService,
            RingMetadata ringMetadata, HashRange rangeManagedByServer) {
        this.dataAccessService = dataAccessService;
        this.ringMetadata = ringMetadata;
        this.rangeManagedByServer = rangeManagedByServer;
    }

    @Override
    public KVAdminMessage execute() {
        LOGGER.debug("Executing update ring metadata write request.");
        dataAccessService.setRingMetadata(ringMetadata);
        dataAccessService.setManagedHashRange(rangeManagedByServer);
        LOGGER.debug("Update ring metadata write request finished successfully.");
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_SUCCESS).build();
    }

    private KVAdminMessage createErrorKVAdminMessage(String responseMessage) {
        return new KVAdminMessage.Builder().status(StatusType.RESPONSE_ERROR)
                .responseMessage(responseMessage).build();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateHashRange(rangeManagedByServer);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Hash range managed by server cannot be null.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        try {
            KVServerRequestsValidator.validateRingMetadata(ringMetadata);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Ring metadata cannot be null.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

}
