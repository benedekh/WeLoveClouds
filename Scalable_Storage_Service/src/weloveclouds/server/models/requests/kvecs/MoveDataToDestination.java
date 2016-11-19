package weloveclouds.server.models.requests.kvecs;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.models.MovableStorageUnits;

public class MoveDataToDestination implements IKVECSRequest {

    private IMovableDataAccessService dataAccessService;
    private RingMetadataPart targetServerInfo;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;
    private ICommunicationApi communicationApi;


    public MoveDataToDestination(IMovableDataAccessService dataAccessService,
            RingMetadataPart targetServerInfo, ICommunicationApi communicationApi) {
        this.dataAccessService = dataAccessService;
        this.targetServerInfo = targetServerInfo;
        this.communicationApi = communicationApi;
    }

    @Override
    public KVAdminMessage execute() {
        HashRange hashRange = targetServerInfo.getRange();
        MovableStorageUnits filteredEntries = dataAccessService.filterEntries(hashRange);

        if (!filteredEntries.getStorageUnits().isEmpty()) {
            try {
                KVTransferMessage transferMessage = new KVTransferMessage.Builder()
                        .status(StatusType.TRANSFER).storageUnits(filteredEntries).build();

                try {
                    communicationApi.disconnect();
                } catch (Exception ex) {
                    // suppress exception
                }

                communicationApi.connectTo(targetServerInfo.getConnectionInfo());
                SerializedMessage serializedMessage =
                        transferMessageSerializer.serialize(transferMessage);
                communicationApi.send(serializedMessage.getBytes());
                KVTransferMessage response =
                        transferMessageDeserializer.deserialize(communicationApi.receive());

                if (response.getStatus() == StatusType.TRANSFER_ERROR) {
                    return createErrorKVAdminMessage(response.getResponseMessage());
                } else {
                    dataAccessService.removeEntries(hashRange);
                    dataAccessService.defragment();
                }
            } catch (Exception e) {
                return createErrorKVAdminMessage(e.getMessage());
            } finally {
                try {
                    communicationApi.disconnect();
                } catch (Exception ex) {
                    // suppress exception
                }
            }
        }

        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS)
                .build();
    }

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
    }

}
