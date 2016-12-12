package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.commons.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVTRANSFER_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.PUTABLE_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.REMOVABLE_KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.RESPONSE_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNITS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.KVEntryDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.MovableStorageUnitsSetDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link KVTransferMessage}.
 * 
 * @author Benedek
 */
public class KVTransferMessageDeserializer
        implements IMessageDeserializer<KVTransferMessage, SerializedMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageDeserializer.class);

    private IDeserializer<Set<MovableStorageUnit>, String> storageUnitsDeserializer =
            new MovableStorageUnitsSetDeserializer();
    private IDeserializer<KVEntry, String> kvEntryDeserializer = new KVEntryDeserializer();

    @Override
    public KVTransferMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public KVTransferMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVTransferMessage from byte[].");
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);

        try {
            Matcher transferMessageMatcher =
                    getRegexFromToken(KVTRANSFER_MESSAGE).matcher(serializedMessageStr);
            if (transferMessageMatcher.find()) {
                String serializedTransferMessage = transferMessageMatcher.group(XML_NODE);

                KVTransferMessage deserialized = new KVTransferMessage.Builder()
                        .status(deserializeStatus(serializedTransferMessage))
                        .storageUnits(deserializeStorageUnits(serializedTransferMessage))
                        .putableEntry(deserializePutableEntry(serializedTransferMessage))
                        .removableKey(deserializeString(serializedTransferMessage, REMOVABLE_KEY))
                        .responseMessage(
                                deserializeString(serializedTransferMessage, RESPONSE_MESSAGE))
                        .build();

                LOGGER.debug("KVTransferMessage deserialization finished.");
                return deserialized;
            } else {
                throw new DeserializationException(CustomStringJoiner.join("",
                        "Unable to extract KVTransferMessage from:", serializedMessageStr));
            }
        } catch (Exception ex) {
            throw new DeserializationException(ex.getMessage());
        }
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
                    CustomStringJoiner.join("", "Unable to extract status from:", from));
        }
    }

    private KVEntry deserializePutableEntry(String from) throws DeserializationException {
        Matcher entryMatcher = getRegexFromToken(PUTABLE_ENTRY).matcher(from);
        if (entryMatcher.find()) {
            return kvEntryDeserializer.deserialize(entryMatcher.group(XML_NODE));
        } else {
            return null;
        }
    }

    private String deserializeString(String from, String token) throws DeserializationException {
        Matcher stringMatcher = getRegexFromToken(token).matcher(from);
        if (stringMatcher.find()) {
            return stringMatcher.group(XML_NODE);
        } else {
            return null;
        }
    }

    private Set<MovableStorageUnit> deserializeStorageUnits(String from)
            throws DeserializationException {
        Matcher storageUnitsMatcher = getRegexFromToken(STORAGE_UNITS).matcher(from);
        if (storageUnitsMatcher.find()) {
            return storageUnitsDeserializer.deserialize(storageUnitsMatcher.group(XML_NODE));
        } else {
            return null;
        }
    }

}
