package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVTRANSACTION_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.OTHER_PARTICIPANTS;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.TRANSACTION_ID;
import static weloveclouds.commons.serialization.models.XMLTokens.TRANSFER_MESSAGE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfosSetDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.TransferMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.UUIDDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.proxy.KVTransactionMessageProxy;
import weloveclouds.commons.kvstore.models.messages.proxy.KVTransferMessageProxy;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link IKVTransactionMessage}.
 * 
 * @author Benedek
 */
public class KVTransactionMessageDeserializer
        implements IMessageDeserializer<IKVTransactionMessage, SerializedMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVTransactionMessageDeserializer.class);

    private IDeserializer<UUID, String> transactionIdDeserializer = new UUIDDeserializer();
    private IDeserializer<IKVTransferMessage, String> transferMessageDeserializer =
            new TransferMessageDeserializer();
    private IDeserializer<Set<ServerConnectionInfo>, String> otherParticipantsDeserializer =
            new ServerConnectionInfosSetDeserializer();

    @Override
    public IKVTransactionMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public IKVTransactionMessage deserialize(byte[] serializedMessage)
            throws DeserializationException {
        LOGGER.debug("Deserializing KVTransactionMessage from byte[].");
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        IKVTransactionMessage deserialized;

        try {
            Matcher transactionMessageMatcher =
                    getRegexFromToken(KVTRANSACTION_MESSAGE).matcher(serializedMessageStr);
            if (transactionMessageMatcher.find()) {
                String serializedTransactionMessage = transactionMessageMatcher.group(XML_NODE);

                if (StringUtils.stringIsNotEmpty(serializedTransactionMessage)) {
                    IKVTransferMessage transferMessage = null;
                    try {
                        transferMessage = new KVTransferMessageProxy(
                                deserializeTransferPayload(serializedTransactionMessage));
                    } catch (DeserializationException ex) {
                        LOGGER.error(ex);
                    }

                    KVTransactionMessage transactionMessage = new KVTransactionMessage.Builder()
                            .status(deserializeStatus(serializedTransactionMessage))
                            .transactionId(deserializeTransactionId(serializedTransactionMessage))
                            .transferPayload(transferMessage)
                            .otherParticipants(
                                    deserializeOtherParticipants(serializedTransactionMessage))
                            .build();
                    deserialized = new KVTransactionMessageProxy(transactionMessage);
                    LOGGER.debug(StringUtils.join(" ", "Deserialized KVTransactionMessage is:",
                            deserialized));
                } else {
                    throw new DeserializationException("KVTransactionMessage is empty.");
                }
            } else {
                throw new DeserializationException(StringUtils.join("",
                        "Unable to extract KVTransactionMessage from:", serializedMessageStr));
            }
        } catch (Exception ex) {
            throw new DeserializationException(ex.getMessage());
        }

        return deserialized;
    }

    private StatusType deserializeStatus(String from) throws DeserializationException {
        Matcher statusMatcher = getRegexFromToken(STATUS).matcher(from);
        if (statusMatcher.find()) {
            String statusStr = statusMatcher.group(XML_NODE);
            try {
                return StatusType.valueOf(statusStr);
            } catch (IllegalArgumentException ex) {
                throw new DeserializationException("StatusType is not recognized.");
            }
        } else {
            throw new DeserializationException(
                    StringUtils.join("", "Unable to extract status from:", from));
        }
    }

    private UUID deserializeTransactionId(String from) throws DeserializationException {
        Matcher transactionIdMatcher = getRegexFromToken(TRANSACTION_ID).matcher(from);
        if (transactionIdMatcher.find()) {
            return transactionIdDeserializer.deserialize(transactionIdMatcher.group(XML_NODE));
        } else {
            throw new DeserializationException(
                    StringUtils.join("", "Unable to transaction ID from:", from));
        }
    }

    private IKVTransferMessage deserializeTransferPayload(String from)
            throws DeserializationException {
        Matcher transferMessageMatcher = getRegexFromToken(TRANSFER_MESSAGE).matcher(from);
        if (transferMessageMatcher.find()) {
            return transferMessageDeserializer.deserialize(transferMessageMatcher.group(XML_NODE));
        }
        return null;
    }

    private Set<ServerConnectionInfo> deserializeOtherParticipants(String from)
            throws DeserializationException {
        Matcher otherParticipantsMatcher = getRegexFromToken(OTHER_PARTICIPANTS).matcher(from);
        if (otherParticipantsMatcher.find()) {
            return otherParticipantsDeserializer
                    .deserialize(otherParticipantsMatcher.group(XML_NODE));
        }
        return null;
    }

}
