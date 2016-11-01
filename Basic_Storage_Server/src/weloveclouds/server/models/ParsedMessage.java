package weloveclouds.server.models;

import weloveclouds.kvstore.IKVMessage.StatusType;


/**
 * Created by Benoit on 2016-10-30.
 */
public class ParsedMessage {
    StatusType statusType;

    public StatusType getStatusType() {
        return statusType;
    }
}
