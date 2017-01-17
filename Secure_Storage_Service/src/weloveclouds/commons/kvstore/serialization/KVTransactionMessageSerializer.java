package weloveclouds.commons.kvstore.serialization;

import static weloveclouds.commons.serialization.models.XMLTokens.KVTRANSACTION_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.OTHER_PARTICIPANTS;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.models.XMLTokens.TRANSACTION_ID;
import static weloveclouds.commons.serialization.models.XMLTokens.TRANSFER_MESSAGE;

import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransactionMessage.StatusType;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfosIterableSerializer;
import weloveclouds.commons.kvstore.serialization.helper.TransferMessageSerializer;
import weloveclouds.commons.kvstore.serialization.helper.UUIDSerializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link IKVTransactionMessage} to a {@link SerializedMessage}.
 * 
 * @author Benoit
 */
public class KVTransactionMessageSerializer
        implements IMessageSerializer<SerializedMessage, IKVTransactionMessage> {

    private static final Logger LOGGER = Logger.getLogger(KVTransactionMessageSerializer.class);

    private ISerializer<AbstractXMLNode, UUID> transactionIdSerializer = new UUIDSerializer();
    private ISerializer<AbstractXMLNode, IKVTransferMessage> transferMessageSerializer =
            new TransferMessageSerializer();
    private ISerializer<AbstractXMLNode, Iterable<ServerConnectionInfo>> otherParticipantsSerializer =
            new ServerConnectionInfosIterableSerializer();

    @Override
    public SerializedMessage serialize(IKVTransactionMessage unserializedMessage) {
        LOGGER.debug("Serializing KVTransactionMessage.");
        StatusType status = unserializedMessage.getStatus();

        String message = new XMLRootNode.Builder().token(KVTRANSACTION_MESSAGE)
                .addInnerNode(new XMLNode(STATUS, status == null ? null : status.toString()))
                .addInnerNode(new XMLNode(TRANSACTION_ID,
                        transactionIdSerializer.serialize(unserializedMessage.getTransactionId())
                                .toString()))
                .addInnerNode(new XMLNode(TRANSFER_MESSAGE,
                        transferMessageSerializer
                                .serialize(unserializedMessage.getTransferPayload()).toString()))
                .addInnerNode(new XMLNode(OTHER_PARTICIPANTS,
                        otherParticipantsSerializer
                                .serialize(unserializedMessage.getOtherParticipants()).toString()))
                .build().toString();

        LOGGER.debug(
                StringUtils.join("", "KVTransactionMessage serialization finished: ", message));
        return new SerializedMessage(message);
    }

}
