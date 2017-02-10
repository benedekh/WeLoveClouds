package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.*;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;

/**
 * A serializer which converts a {@link Iterable<HashRange>} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class HashRangesIterableSerializer
        implements ISerializer<AbstractXMLNode, Iterable<HashRange>> {

    private ISerializer<AbstractXMLNode, HashRange> hashRangeSerializer = new HashRangeSerializer();

    @Override
    public AbstractXMLNode serialize(Iterable<HashRange> target) {
        Builder builder = new XMLRootNode.Builder().token(HASH_RANGES);

        if (target != null) {
            for (HashRange range : target) {
                builder.addInnerNode(
                        new XMLNode(HASH_RANGE, hashRangeSerializer.serialize(range).toString()));
            }
        }

        return builder.build();
    }
}
