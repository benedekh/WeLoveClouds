package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNIT;
import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNITS;

import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A serializer which converts a {@link Iterable<MovableStorageUnit>} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class MovableStorageUnitsIterableSerializer
        implements ISerializer<AbstractXMLNode, Iterable<MovableStorageUnit>> {

    private ISerializer<AbstractXMLNode, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Override
    public AbstractXMLNode serialize(Iterable<MovableStorageUnit> target) {
        Builder builder = new XMLRootNode.Builder().token(STORAGE_UNITS);

        if (target != null) {
            for (MovableStorageUnit storageUnit : target) {
                builder.addInnerNode(new XMLNode(STORAGE_UNIT,
                        storageUnitSerializer.serialize(storageUnit).toString()));
            }
        }

        return builder.build();
    }

}
