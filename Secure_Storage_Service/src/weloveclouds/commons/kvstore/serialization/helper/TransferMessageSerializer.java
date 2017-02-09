package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KVTRANSFER_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.PUTABLE_ENTRY;
import static weloveclouds.commons.serialization.models.XMLTokens.REMOVABLE_KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.RESPONSE_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNITS;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A serializer which converts a {@link IKVTransferMessage} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class TransferMessageSerializer implements ISerializer<AbstractXMLNode, IKVTransferMessage> {

    private ISerializer<AbstractXMLNode, Iterable<MovableStorageUnit>> storageUnitsSerializer =
            new MovableStorageUnitsIterableSerializer();
    private ISerializer<AbstractXMLNode, KVEntry> kvEntrySerializer = new KVEntrySerializer();

    @Override
    public AbstractXMLNode serialize(IKVTransferMessage target) {
        Builder builder = new XMLRootNode.Builder().token(KVTRANSFER_MESSAGE);

        if (target != null) {
            StatusType status = target.getStatus();
            builder.addInnerNode(new XMLNode(STATUS, status == null ? null : status.toString()))
                    .addInnerNode(new XMLNode(STORAGE_UNITS,
                            storageUnitsSerializer.serialize(target.getStorageUnits()).toString()))
                    .addInnerNode(new XMLNode(PUTABLE_ENTRY,
                            kvEntrySerializer.serialize(target.getPutableEntry()).toString()))
                    .addInnerNode(new XMLNode(REMOVABLE_KEY, target.getRemovableKey()))
                    .addInnerNode(new XMLNode(RESPONSE_MESSAGE, target.getResponseMessage()));
        }

        return builder.build();
    }

}
