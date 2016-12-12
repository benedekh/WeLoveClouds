package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA_PART;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;;

/**
 * A deserializer which converts a {@link RingMetadata} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataDeserializer implements IDeserializer<RingMetadata, String> {

    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();

    @Override
    public RingMetadata deserialize(String from) throws DeserializationException {
        RingMetadata deserialized = null;

        if (from != null && !"null".equals(from)) {
            try {
                Set<RingMetadataPart> metadataParts = new HashSet<>();

                Matcher metadataPartMatcher = getRegexFromToken(RING_METADATA_PART).matcher(from);
                while (metadataPartMatcher.find()) {
                    metadataParts.add(metadataPartDeserializer
                            .deserialize(metadataPartMatcher.group(XML_NODE)));
                }

                if (metadataParts.isEmpty()) {
                    throw new DeserializationException(CustomStringJoiner.join("",
                            "Unable to extract ring metadata parts from:", from));
                }

                deserialized = new RingMetadata(metadataParts);
            } catch (Exception ex) {
                new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}
