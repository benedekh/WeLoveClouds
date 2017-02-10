package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.KV_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.VALUE;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;

/**
 * A serializer which converts a {@link KVEntry} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class KVEntrySerializer implements ISerializer<AbstractXMLNode, KVEntry> {

    @Override
    public AbstractXMLNode serialize(KVEntry target) {
        Builder builder = new XMLRootNode.Builder().token(KV_ENTRY);

        if (target != null) {
            builder.addInnerNode(new XMLNode(KEY, target.getKey()))
                    .addInnerNode(new XMLNode(VALUE, target.getValue()));
        }

        return builder.build();
    }

}
