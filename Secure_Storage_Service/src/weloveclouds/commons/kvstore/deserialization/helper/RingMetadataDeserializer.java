package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA_PART;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.utils.StringUtils;;

/**
 * A deserializer which converts a {@link String} to a {@link RingMetadata}.
 * 
 * @author Benedek, Hunton
 */
public class RingMetadataDeserializer implements IDeserializer<RingMetadata, String> {

    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();

    @Override
    public RingMetadata deserialize(String from) throws DeserializationException {
        RingMetadata deserialized = null;
        if (StringUtils.stringIsNotEmpty(from)) {
            try {
                Set<RingMetadataPart> metadataParts = new HashSet<>();
                Matcher metadataPartMatcher = getRegexFromToken(RING_METADATA_PART).matcher(from);
                while (metadataPartMatcher.find()) {
                    metadataParts.add(metadataPartDeserializer
                            .deserialize(metadataPartMatcher.group(XML_NODE)));
                }
                if (metadataParts.isEmpty()) {
                    return null;
                }
                deserialized = new RingMetadata(metadataParts);
            } catch (Exception ex) {
                throw new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
