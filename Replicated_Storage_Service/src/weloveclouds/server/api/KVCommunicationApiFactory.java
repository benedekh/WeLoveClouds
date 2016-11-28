package weloveclouds.server.api;

import weloveclouds.commons.communication.NetworkPacketResenderFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.api.v1.KVCommunicationApiV1;

/**
 * Factory for creating {@link IKVCommunicationApi} instances which are used for communication.
 * 
 * @author Benoit
 */
public class KVCommunicationApiFactory extends CommunicationApiFactory {

    public IKVCommunicationApi createKVCommunicationApiV1() {
        return new KVCommunicationApiV1(createCommunicationApiV1(), new KVMessageSerializer(),
                new KVMessageDeserializer(), new NetworkPacketResenderFactory());
    }
}
