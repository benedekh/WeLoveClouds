package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.BEGIN;
import static weloveclouds.commons.serialization.models.XMLTokens.END;
import static weloveclouds.commons.serialization.models.XMLTokens.HASH_RANGE;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;

/**
 * A serializer which converts a {@link HashRange} to a {@link AbstractXMLNode}.
 *
 * @author Benedek, Hunton
 */
public class HashRangeSerializer implements ISerializer<AbstractXMLNode, HashRange> {

    private ISerializer<String, Hash> hashSerializer = new HashSerializer();

    @Override
    public AbstractXMLNode serialize(HashRange target) {
        Builder builder = new XMLRootNode.Builder().token(HASH_RANGE);

        if (target != null) {
            builder.addInnerNode(new XMLNode(BEGIN, hashSerializer.serialize(target.getStart())))
                    .addInnerNode(new XMLNode(END, hashSerializer.serialize(target.getEnd())));
        }

        return builder.build();
    }

}
