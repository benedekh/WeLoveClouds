package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFO;
import static weloveclouds.commons.serialization.models.XMLTokens.READ_RANGES;
import static weloveclouds.commons.serialization.models.XMLTokens.RING_METADATA_PART;
import static weloveclouds.commons.serialization.models.XMLTokens.WRITE_RANGE;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link RingMetadataPart} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class RingMetadataPartSerializer implements ISerializer<AbstractXMLNode, RingMetadataPart> {

    private ISerializer<AbstractXMLNode, ServerConnectionInfo> connectionInfoSerializer =
            new ServerConnectionInfoSerializer();
    private ISerializer<AbstractXMLNode, Iterable<HashRange>> hashRangesSerializer =
            new HashRangesIterableSerializer();
    private ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public AbstractXMLNode serialize(RingMetadataPart target) {
        Builder builder = new XMLRootNode.Builder().token(RING_METADATA_PART);

        if (target != null) {
            builder.addInnerNode(new XMLNode(CONNECTION_INFO,
                    connectionInfoSerializer.serialize(target.getConnectionInfo()).toString()))
                    .addInnerNode(new XMLNode(READ_RANGES,
                            hashRangesSerializer.serialize(target.getReadRanges()).toString()))
                    .addInnerNode(new XMLNode(WRITE_RANGE,
                            hashRangeSerializer.serialize(target.getWriteRange()).toString()));
        }

        return builder.build();
    }

}
