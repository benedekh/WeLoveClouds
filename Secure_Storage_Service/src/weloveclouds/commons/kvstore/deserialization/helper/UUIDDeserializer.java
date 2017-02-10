/**
 * 
 */
package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.ID;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.UUID;
import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link UUID} to a {@link IKVTransferMessage}.
 * 
 * @author Benedek, Hunton
 */
public class UUIDDeserializer implements IDeserializer<UUID, String> {

    @Override
    public UUID deserialize(String from) throws DeserializationException {
        UUID deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                deserialized = deserializeUUID(from);
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }
        return deserialized;
    }

    private UUID deserializeUUID(String from) throws DeserializationException {
        Matcher uuidMatcher = getRegexFromToken(ID).matcher(from);
        if (uuidMatcher.find()) {
            String uuidStr = uuidMatcher.group(XML_NODE);
            try {
                return UUID.fromString(uuidStr);
            } catch (IllegalArgumentException ex) {
                throw new DeserializationException(StringUtils.join(": ", "Invalid UUID", uuidStr));
            }
        }
        return null;
    }

}
