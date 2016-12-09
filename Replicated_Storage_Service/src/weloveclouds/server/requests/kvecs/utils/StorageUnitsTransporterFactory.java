package weloveclouds.server.requests.kvecs.utils;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

/**
 * A factory to create a {@link StorageUnitsTransporter} instance.
 * 
 * @author Benedek
 */
public class StorageUnitsTransporterFactory {

    /**
     * A factory method to create a {@link StorageUnitsTransporter} instance based on its arguments.
     * 
     * @param communicationApi to communicate with the target server
     * @param connectionInfo the IP + port of the destination
     * @param transferMessageSerializer to serialize {@link KVTransferMessage} into
     *        {@link SerializedMessage}
     * @param transferMessageDeserializer to deserialize {@link KVTransferMessage} from
     *        {@link SerializedMessage}
     */
    public StorageUnitsTransporter createStorageUnitsTransporter(ICommunicationApi communicationApi,
            ServerConnectionInfo connectionInfo,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer) {
        return new StorageUnitsTransporter(communicationApi, connectionInfo,
                transferMessageSerializer, transferMessageDeserializer);
    }
}
