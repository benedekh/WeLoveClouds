package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.PUTABLE_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.REMOVABLE_KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.RESPONSE_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNITS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link String} to a {@link IKVTransferMessage}.
 * 
 * @author Benedek, Hunton
 */
public class TransferMessageDeserializer implements IDeserializer<IKVTransferMessage, String> {

    private IDeserializer<Set<MovableStorageUnit>, String> storageUnitsDeserializer =
            new MovableStorageUnitsSetDeserializer();
    private IDeserializer<KVEntry, String> kvEntryDeserializer = new KVEntryDeserializer();

    @Override
    public IKVTransferMessage deserialize(String from) throws DeserializationException {
        IKVTransferMessage deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                deserialized = new KVTransferMessage.Builder().status(deserializeStatus(from))
                        .storageUnits(deserializeStorageUnits(from))
                        .putableEntry(deserializePutableEntry(from))
                        .removableKey(deserializeString(from, REMOVABLE_KEY))
                        .responseMessage(deserializeString(from, RESPONSE_MESSAGE)).build();
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
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
