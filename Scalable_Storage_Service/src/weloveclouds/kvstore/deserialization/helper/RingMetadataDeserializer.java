package weloveclouds.kvstore.deserialization.helper;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.helper.RingMetadataSerializer;;

public class RingMetadataDeserializer implements IDeserializer<RingMetadata, String> {

    private static final Logger LOGGER = Logger.getLogger(RingMetadataDeserializer.class);
    
    private IDeserializer<RingMetadataPart, String> metadataPartDeserializer = new RingMetadataPartDeserializer();

    @Override
    public RingMetadata deserialize(String from) throws DeserializationException {
        RingMetadata deserialized = null;

        if (from != null && !"null".equals(from)) {
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
