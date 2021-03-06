package weloveclouds.server.models.requests;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler (see package {@link weloveclouds.server.models.requests}.
 *
 * @author Benoit
 */
public class RequestFactory {
    private IDataAccessService dataAccessService;

    private Logger logger;

    public RequestFactory(IDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
        this.logger = Logger.getLogger(getClass());
    }

    public IRequest createRequestFromReceivedMessage(KVMessage receivedMessage) {
        IRequest request = null;
        StatusType status = receivedMessage.getStatus();

        // see M2 docs, we delete the key if it is a PUT request with only a key
        if (receivedMessage.getStatus() == StatusType.PUT && receivedMessage.getValue() == null) {
            status = StatusType.DELETE;
        }

        switch (status) {
            case GET:
                request = new Get(dataAccessService, receivedMessage.getKey());
                break;
            case PUT:
                request = new Put(dataAccessService, receivedMessage.getKey(),
                        receivedMessage.getValue());
                break;
            case DELETE:
                request = new Delete(dataAccessService, receivedMessage.getKey());
                break;
            default:
                String errorMessage = "Unrecognized command for KV message";
                synchronized (logger) {
                    logger.error(join(" ", errorMessage, receivedMessage.toString()));
                }
                request = new DefaultRequest(receivedMessage.getKey(), errorMessage);
                break;
        }

        return request;
    }
}
