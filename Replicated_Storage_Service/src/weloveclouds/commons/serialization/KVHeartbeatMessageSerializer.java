package weloveclouds.commons.serialization;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.SerializationConstants;
import weloveclouds.loadbalancer.models.KVHearthbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-09.
 */
public class KVHeartbeatMessageSerializer implements IMessageSerializer<SerializedMessage, KVHearthbeatMessage> {
    private ISerializer<String, NodeHealthInfos> nodeHealthInfosStringSerializer;

    public KVHeartbeatMessageSerializer(ISerializer<String, NodeHealthInfos> nodeHealthInfosStringSerializer) {
        this.nodeHealthInfosStringSerializer = nodeHealthInfosStringSerializer;
    }

    @Override
    public SerializedMessage serialize(KVHearthbeatMessage unserializedMessage) {
        return new SerializedMessage(CustomStringJoiner.join("",
                SerializationConstants.KVHEARTHBEAT_MESSAGE_START_TOKEN,
                nodeHealthInfosStringSerializer.serialize(unserializedMessage.getNodeHealthInfos()),
                SerializationConstants.KVHEARTHBEAT_MESSAGE_END_TOKEN));
    }
}
