package weloveclouds.server.requests.kvclient;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.services.datastore.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benoit
 */
public class KVClientRequestFactory implements IRequestFactory<IKVMessage, IKVClientRequest> {

    private static final Logger LOGGER = Logger.getLogger(Put.class);

    private IMovableDataAccessService dataAccessService;
    private ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer;

    public KVClientRequestFactory(IMovableDataAccessService dataAccessService,
            ISerializer<AbstractXMLNode, RingMetadata> ringMetadataSerializer) {
        this.dataAccessService = dataAccessService;
        this.ringMetadataSerializer = ringMetadataSerializer;
    }

    @Override
    public IKVClientRequest createRequestFromReceivedMessage(IKVMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVClientRequest request = null;
        StatusType status = receivedMessage.getStatus();

        // see M2 docs, we delete the key if it is a PUT request with only a key
        if (receivedMessage.getStatus() == StatusType.PUT && receivedMessage.getValue() == null) {
            status = StatusType.DELETE;
        }

        switch (status) {
            case GET:
                request = new Get.Builder().dataAccessService(dataAccessService)
                        .key(receivedMessage.getKey())
                        .ringMetadataSerializer(ringMetadataSerializer).build();
                break;
            case PUT:
                request = new Put.Builder().dataAccessService(dataAccessService)
                        .key(receivedMessage.getKey()).value(receivedMessage.getValue())
                        .ringMetadataSerializer(ringMetadataSerializer).build();
                break;
            case DELETE:
                request = new Delete.Builder().dataAccessService(dataAccessService)
                        .key(receivedMessage.getKey())
                        .ringMetadataSerializer(ringMetadataSerializer).build();
                break;
            default:
                String errorMessage = "Unrecognized command for KV message";
                LOGGER.error(StringUtils.join(" ", errorMessage, receivedMessage));
                request = new DefaultRequest(receivedMessage.getKey(), errorMessage);
                break;
        }

        return request;
    }

}
