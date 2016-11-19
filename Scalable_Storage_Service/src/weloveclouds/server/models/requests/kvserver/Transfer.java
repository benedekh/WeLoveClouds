package weloveclouds.server.models.requests.kvserver;

import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnits;

public class Transfer implements IKVServerRequest {

    private IMovableDataAccessService dataAccessService;
    private MovableStorageUnits storageUnits;

    public Transfer(IMovableDataAccessService dataAccessService, MovableStorageUnits storageUnits) {
        this.dataAccessService = dataAccessService;
        this.storageUnits = storageUnits;
    }

    @Override
    public KVTransferMessage execute() {
        try {
            dataAccessService.putEntries(storageUnits);
            return new KVTransferMessage.KVTransferMessageBuilder()
                    .status(StatusType.TRANSFER_SUCCESS).build();
        } catch (StorageException ex) {
            return new KVTransferMessage.KVTransferMessageBuilder()
                    .status(StatusType.TRANSFER_ERROR).responseMessage(ex.getMessage()).build();
        }
    }

}
