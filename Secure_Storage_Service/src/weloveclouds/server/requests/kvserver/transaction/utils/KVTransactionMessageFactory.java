package weloveclouds.server.requests.kvserver.transaction.utils;

import java.util.UUID;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransactionMessage;

public class KVTransactionMessageFactory {

    public static KVTransactionMessage createUnknownIDTransactionResponse(UUID transactionId) {
        return createTransactionResponse(transactionId, StatusType.RESPONSE_UNKNOWN_ID);
    }

    public static KVTransactionMessage createTransactionResponse(UUID transactionId,
            StatusType status) {
        return new KVTransactionMessage.Builder().status(status).transactionId(transactionId)
                .build();
    }

}

