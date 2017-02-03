package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KV_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNIT;

import java.util.Map.Entry;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A serializer which converts a {@link MovableStorageUnit} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class MovableStorageUnitSerializer
        implements ISerializer<AbstractXMLNode, MovableStorageUnit> {

    private ISerializer<AbstractXMLNode, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public AbstractXMLNode serialize(MovableStorageUnit target) {
        Builder builder = new XMLRootNode.Builder().token(STORAGE_UNIT);

        if (target != null) {
            for (Entry<String, String> entry : target.getEntries()) {
                builder.addInnerNode(new XMLNode(KV_ENTRY, kvEntrySerializer
                        .serialize(new KVEntry(entry.getKey(), entry.getValue())).toString()));
            }
        }

        return builder.build();
    }

}
