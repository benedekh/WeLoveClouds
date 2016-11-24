package weloveclouds.server.api;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.api.v1.KVCommunicationApiV1;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVCommunicationApiFactory extends CommunicationApiFactory {
    public IKVCommunicationApi createKVCommunicationApiV1() {
        return new KVCommunicationApiV1(createCommunicationApiV1(), new KVMessageSerializer(),
                new KVMessageDeserializer());
    }
}
