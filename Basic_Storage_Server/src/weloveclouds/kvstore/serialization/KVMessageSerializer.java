package weloveclouds.commons.kvstore.serialization;

import org.apache.log4j.Logger;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import weloveclouds.commons.kvstore.models.KVMessage;
import weloveclouds.commons.kvstore.serialization.models.SerializedKVMessage;
import weloveclouds.commons.kvstore.utils.KVMessageUtils;

/**
 * An exact deserializer which converts a {@link KVMessage} to a {@link SerializedKVMessage}.
 * 
 * @author Benoit
 */
public class KVMessageSerializer implements IMessageSerializer<SerializedKVMessage, KVMessage> {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public SerializedKVMessage serialize(KVMessage unserializedMessage) {
        logger.debug(join(" ", "Serializing message:", unserializedMessage.toString()));
        return new SerializedKVMessage(
                KVMessageUtils.convertMessageToString(unserializedMessage).getBytes());
    }
}
