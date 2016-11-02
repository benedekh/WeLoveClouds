package weloveclouds.server.models.requests;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.IKVMessage.StatusType;
import weloveclouds.kvstore.models.KVMessage;
import weloveclouds.server.services.IDataAccessService;

/**
 * Created by Benoit on 2016-10-31.
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
