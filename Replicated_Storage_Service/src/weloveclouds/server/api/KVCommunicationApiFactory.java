package weloveclouds.server.api;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.api.v1.KVCommunicationApiV1;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.api.v2.KVCommunicationApiV2;

/**
 * Created by Benoit on 2016-11-15.
 */
public class KVCommunicationApiFactory extends CommunicationApiFactory {

    public IKVCommunicationApi createKVCommunicationApiV1() {
        return new KVCommunicationApiV1(createCommunicationApiV1(), new KVMessageSerializer(),
                new KVMessageDeserializer());
    }

    public IKVCommunicationApiV2 createKVCommunicationApiV2(
            ServerConnectionInfo bootstrapConnectionInfo) {
        return new KVCommunicationApiV2(bootstrapConnectionInfo);
    }
}
