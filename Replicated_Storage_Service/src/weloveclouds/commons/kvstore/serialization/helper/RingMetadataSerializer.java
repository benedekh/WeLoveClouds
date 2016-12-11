package weloveclouds.commons.kvstore.serialization.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.client.utils.CustomStringJoiner;


/**
 * A serializer which converts a {@link RingMetadata} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataSerializer implements ISerializer<String, RingMetadata> {

    public static final String SEPARATOR = "-łł-";
    private static final Logger LOGGER = Logger.getLogger(RingMetadata.class);

    private ISerializer<String, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Override
    public String serialize(RingMetadata target) {
        String serialized = null;

        if (target != null) {
            LOGGER.debug("Serializing a RingMetadata.");
            // original fields
            Set<RingMetadataPart> metadataParts = target.getMetadataParts();

            // string representation
            Set<String> metadataPartsStrs = new HashSet<>();
            for (RingMetadataPart metadataPart : metadataParts) {
                metadataPartsStrs.add(metadataPartSerializer.serialize(metadataPart));
            }

            // merged string representation
            serialized = CustomStringJoiner.join(SEPARATOR, new ArrayList<>(metadataPartsStrs));
            LOGGER.debug("Serializing a RingMetadata finished.");
        }

        return serialized;
    }

}
