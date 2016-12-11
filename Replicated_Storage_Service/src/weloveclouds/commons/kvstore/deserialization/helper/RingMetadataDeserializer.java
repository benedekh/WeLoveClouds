package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataSerializer;;

/**
 * A deserializer which converts a {@link RingMetadata} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataDeserializer implements IDeserializer<RingMetadata, String> {

    private static final Logger LOGGER = Logger.getLogger(RingMetadataDeserializer.class);

    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer =
            new RingMetadataPartDeserializer();

    @Override
    public RingMetadata deserialize(String from) throws DeserializationException {
        RingMetadata deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a RingMetadata from String.");
            // raw message split
            String[] parts = from.split(RingMetadataSerializer.SEPARATOR);

            // deserialized fields
            Set<RingMetadataPart> metadataParts = new HashSet<>();
            for (String serializedPart : parts) {
                metadataParts.add(metadataPartDeserializer.deserialize(serializedPart));
            }

            // deserialized object
            deserialized = new RingMetadata(metadataParts);
            LOGGER.debug(join(" ", "Deserialized ring metadata is:", deserialized.toString()));
        }

        return deserialized;
    }

}
