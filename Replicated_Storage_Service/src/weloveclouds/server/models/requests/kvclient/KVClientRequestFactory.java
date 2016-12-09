package weloveclouds.server.models.requests.kvclient;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.core.requests.ICallbackRegister;
import weloveclouds.server.core.requests.IRequestFactory;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benoit
 */
public class KVClientRequestFactory implements IRequestFactory<KVMessage, IKVClientRequest> {

    private static final Logger LOGGER = Logger.getLogger(Put.class);

    private IMovableDataAccessService dataAccessService;
    private ISerializer<String, RingMetadata> ringMetadataSerializer;

    public KVClientRequestFactory(IMovableDataAccessService dataAccessService,
            ISerializer<String, RingMetadata> ringMetadataSerializer) {
        this.dataAccessService = dataAccessService;
        this.ringMetadataSerializer = ringMetadataSerializer;
    }

    @Override
    public IKVClientRequest createRequestFromReceivedMessage(KVMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVClientRequest request = null;
        StatusType status = receivedMessage.getStatus();

        // see M2 docs, we delete the key if it is a PUT request with only a key
        if (receivedMessage.getStatus() == StatusType.PUT && receivedMessage.getValue() == null) {
            status = StatusType.DELETE;
        }

        switch (status) {
            case GET:
                request =
                        new Get(dataAccessService, receivedMessage.getKey(), ringMetadataSerializer);
                break;
            case PUT:
                request = new Put(dataAccessService, receivedMessage.getKey(),
                        receivedMessage.getValue(), ringMetadataSerializer);
                break;
            case DELETE:
                request = new Delete(dataAccessService, receivedMessage.getKey(),
                        ringMetadataSerializer);
                break;
            default:
                String errorMessage = "Unrecognized command for KV message";
                LOGGER.error(join(" ", errorMessage, receivedMessage.toString()));
                request = new DefaultRequest(receivedMessage.getKey(), errorMessage);
                break;
        }

        return request;
    }

}
