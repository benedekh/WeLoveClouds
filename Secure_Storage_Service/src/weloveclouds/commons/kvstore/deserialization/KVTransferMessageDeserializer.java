package weloveclouds.commons.kvstore.deserialization;

import static weloveclouds.commons.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.models.XMLTokens.KVTRANSFER_MESSAGE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.TransferMessageDeserializer;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.proxy.KVTransferMessageProxy;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link SerializedMessage} to a {@link IKVTransferMessage}.
 * 
 * @author Benedek, Hunton
 */
public class KVTransferMessageDeserializer
        implements IMessageDeserializer<IKVTransferMessage, SerializedMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVTransferMessageDeserializer.class);

    private IDeserializer<IKVTransferMessage, String> transferMessageDeserializer =
            new TransferMessageDeserializer();

    @Override
    public IKVTransferMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        return deserialize(serializedMessage.getBytes());
    }

    @Override
    public IKVTransferMessage deserialize(byte[] serializedMessage)
            throws DeserializationException {
        LOGGER.debug("Deserializing KVTransferMessage from byte[].");
        String serializedMessageStr = new String(serializedMessage, MESSAGE_ENCODING);
        IKVTransferMessage deserialized = null;

        try {
            Matcher transferMessageMatcher =
                    getRegexFromToken(KVTRANSFER_MESSAGE).matcher(serializedMessageStr);
            if (transferMessageMatcher.find()) {
                String serializedTransferMessage = transferMessageMatcher.group(XML_NODE);

                if (StringUtils.stringIsNotEmpty(serializedTransferMessage)) {
                    IKVTransferMessage transferMessage =
                            transferMessageDeserializer.deserialize(serializedTransferMessage);
                    deserialized = new KVTransferMessageProxy(transferMessage);
                    LOGGER.debug("KVTransferMessage deserialization finished.");
                } else {
                    throw new DeserializationException("KVTransferMessage is empty.");
                }
            } else {
                throw new DeserializationException(StringUtils.join("",
                        "Unable to extract KVTransferMessage from:", serializedMessageStr));
            }
        } catch (Exception ex) {
            throw new DeserializationException(ex.getMessage());
        }

        return deserialized;
    }

}
