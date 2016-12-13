package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.BEGIN;
import static weloveclouds.commons.serialization.models.XMLTokens.END;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.regex.Matcher;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link String} to a {@link HashRange}.
 * 
 * @author Benedek
 */
public class HashRangeDeserializer implements IDeserializer<HashRange, String> {

    private IDeserializer<Hash, String> hashDeserializer = new HashDeserializer();

    @Override
    public HashRange deserialize(String from) throws DeserializationException {
        HashRange deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                deserialized = new HashRange.Builder().begin(deserializeHash(from, BEGIN))
                        .end(deserializeHash(from, END)).build();
            } catch (Exception ex) {
                new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

    private Hash deserializeHash(String from, String token) throws DeserializationException {
        Matcher hashFieldMatcher = getRegexFromToken(token).matcher(from);
        if (hashFieldMatcher.find()) {
            return hashDeserializer.deserialize(hashFieldMatcher.group(XML_NODE));
        } else {
            throw new DeserializationException(
                    join("", "Unable to extract ", token, " hash range from:", from));
        }
    }

}
