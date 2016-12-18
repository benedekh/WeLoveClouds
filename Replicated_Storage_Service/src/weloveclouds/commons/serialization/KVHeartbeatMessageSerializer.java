package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.loadbalancer.models.KVHeartbeatMessage;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

import static weloveclouds.commons.serialization.models.XMLTokens.KVHEARTBEAT_MESSAGE;
import static weloveclouds.commons.serialization.models.XMLTokens.NAME;

/**
 * Created by Benoit on 2016-12-09.
 */
public class KVHeartbeatMessageSerializer implements
        IMessageSerializer<SerializedMessage, KVHeartbeatMessage> {

    private ISerializer<AbstractXMLNode, ServiceHealthInfos> serviceHealthInfosSerializer;

    @Inject
    public KVHeartbeatMessageSerializer(ISerializer<AbstractXMLNode, ServiceHealthInfos>
                                                serviceHealthInfosSerializer) {
        this.serviceHealthInfosSerializer = serviceHealthInfosSerializer;
    }

    @Override
    public SerializedMessage serialize(KVHeartbeatMessage unserializedMessage) {
        return new SerializedMessage(new XMLRootNode.Builder()
                .token(KVHEARTBEAT_MESSAGE)
                .addInnerNode(new XMLNode(NAME, unserializedMessage.getNodeName()))
                .addInnerNode(serviceHealthInfosSerializer
                        .serialize(unserializedMessage.getServicesHealthInfos()))
                .build()
                .toString());
    }
}
