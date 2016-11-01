package weloveclouds.server.models;

import weloveclouds.kvstore.api.IKVServerApi;
import weloveclouds.server.models.requests.IRequest;

/**
 * Created by Benoit on 2016-10-31.
 */
public class RequestFactory {

    synchronized public IRequest createRequestFromReceivedMessage(ParsedMessage parsedMessage) {
        IRequest request = null;

        switch (parsedMessage.getStatusType()) {

        }

        return request;
    }
}
