package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.HASH_RANGE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;

/**
 * A deserializer which converts a {@link String} to a {@link Set<HashRange>}.
 * 
 * @author Benedek, Hunton
 */
public class HashRangesSetDeserializer implements IDeserializer<Set<HashRange>, String> {

    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public Set<HashRange> deserialize(String from) throws DeserializationException {
        Set<HashRange> deserialized = null;

        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                deserialized = new HashSet<>();
                Matcher hashRangesMatcher = getRegexFromToken(HASH_RANGE).matcher(from);
                while (hashRangesMatcher.find()) {
                    deserialized.add(
                            hashRangeDeserializer.deserialize(hashRangesMatcher.group(XML_NODE)));
                }
                if (deserialized.isEmpty()) {
                    return null;
                }
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
