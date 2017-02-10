package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA;
import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA_PART;

import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;


/**
 * A serializer which converts a {@link RingMetadata} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class RingMetadataSerializer implements ISerializer<AbstractXMLNode, RingMetadata> {

    private ISerializer<AbstractXMLNode, RingMetadataPart> metadataPartSerializer =
            new RingMetadataPartSerializer();

    @Override
    public AbstractXMLNode serialize(RingMetadata target) {
        Builder builder = new XMLRootNode.Builder().token(RING_METADATA);

        if (target != null) {
            for (RingMetadataPart metadataPart : target.getMetadataParts()) {
                builder.addInnerNode(new XMLNode(RING_METADATA_PART,
                        metadataPartSerializer.serialize(metadataPart).toString()));
            }
        }

        return builder.build();
    }

}
