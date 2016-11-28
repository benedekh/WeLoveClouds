package weloveclouds.server.api;

import weloveclouds.commons.communication.NetworkPacketResenderFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.api.v1.KVCommunicationApiV1;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.api.v2.KVCommunicationApiV2;

/**
 * Factory for creating {@link IKVCommunicationApi} and {@link IKVCommunicationApiV2} instances
 * which are used for communication.
 * 
 * @author Benoit
 */
public class KVCommunicationApiFactory extends CommunicationApiFactory {

    public IKVCommunicationApi createKVCommunicationApiV1() {
        return new KVCommunicationApiV1(createCommunicationApiV1(), new KVMessageSerializer(),
                new KVMessageDeserializer(), new NetworkPacketResenderFactory());
    }

    public IKVCommunicationApiV2 createKVCommunicationApiV2(
            ServerConnectionInfo bootstrapConnectionInfo) {
        return new KVCommunicationApiV2(bootstrapConnectionInfo);
    }
}
