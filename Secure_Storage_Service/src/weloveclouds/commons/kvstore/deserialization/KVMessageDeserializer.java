package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVMESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.KV_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.KVEntryDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVMessage;
import weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.models.messages.proxy.KVMessageProxy;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link IKVMessage}.
 * 
 * @author Benoit, Hunton
 */
public class KVMessageDeserializer implements IMessageDeserializer<IKVMessage, SerializedMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVMessageDeserializer.class);

    private IDeserializer<KVEntry, String> kvEntryDeserializer = new KVEntryDeserializer();

    @Override
    public IKVMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public IKVMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        LOGGER.debug("Deserializing KVMessage from byte[].");
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        IKVMessage deserialized = null;
        
        try {
            Matcher kvMessageMatcher = getRegexFromToken(KVMESSAGE).matcher(serializedMessageStr);
            if (kvMessageMatcher.find()) {
                String serializedKVMessage = kvMessageMatcher.group(XML_NODE);

                if (StringUtils.stringIsNotEmpty(serializedKVMessage)) {
                    KVEntry entry = deserializeKVEntry(serializedKVMessage);
                    KVMessage kvMessage =
                            new KVMessage.Builder().status(deserializeStatus(serializedKVMessage))
                                    .key(entry.getKey()).value(entry.getValue()).build();
                    deserialized = new KVMessageProxy(kvMessage);
                    LOGGER.debug(StringUtils.join(" ", "Deserialized KVMessage is:", deserialized));
                } else {
                    throw new DeserializationException("KVMessage is empty.");
                }
            } else {
                throw new DeserializationException(StringUtils.join("",
                        "Unable to extract KVMessage from:", serializedMessageStr));
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

    private KVEntry deserializeKVEntry(String from) throws DeserializationException {
        Matcher kvEntryMatcher = getRegexFromToken(KV_ENTRY).matcher(from);
        if (kvEntryMatcher.find()) {
            return kvEntryDeserializer.deserialize(kvEntryMatcher.group(XML_NODE));
        } else {
            throw new DeserializationException(
                    StringUtils.join("", "Unable to extract KVEntry from:", from));
        }
    }
}
