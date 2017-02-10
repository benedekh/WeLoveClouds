package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.VALUE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link String} to a {@link KVEntry}.
 * 
 * @author Benedek, Hunton
 */
public class KVEntryDeserializer implements IDeserializer<KVEntry, String> {

    @Override
    public KVEntry deserialize(String from) throws DeserializationException {
        KVEntry deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                String key = deserializeField(from, KEY);
                String value = deserializeField(from, VALUE);

                if (key == null && value == null) {
                    return deserialized;
                }

                deserialized = new KVEntry(key, value);
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

    private String deserializeField(String from, String token) throws DeserializationException {
        Matcher fieldMatcher = getRegexFromToken(token).matcher(from);
        if (fieldMatcher.find()) {
            String deserialized = fieldMatcher.group(XML_NODE);
            if (StringUtils.stringIsNotEmpty(deserialized)) {
                return deserialized;
            }
        }
        return null;
    }
}
