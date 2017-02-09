package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.ID;
import static weloveclouds.commons.serialization.models.XMLTokens.UUID;

import java.util.UUID;

import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;

/**
 * A serializer which converts a {@link UUID} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class UUIDSerializer implements ISerializer<AbstractXMLNode, UUID> {

    @Override
    public AbstractXMLNode serialize(UUID target) {
        Builder builder = new XMLRootNode.Builder().token(UUID);

        if (target != null) {
            builder.addInnerNode(new XMLNode(ID, target.toString()));
        }

        return builder.build();
    }

}
