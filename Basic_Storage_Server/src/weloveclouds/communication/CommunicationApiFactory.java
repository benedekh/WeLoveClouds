package weloveclouds.communication;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.commons.kvstore.serialization.KVMessageDeserializer;
import weloveclouds.commons.kvstore.serialization.KVMessageSerializer;

/**
 * Factory for creating {@link ICommunicationApiV1}, {@link IConcurrentCommunicationApi} and
 * {@link IKVCommunicationApi} instances which are used for communication.
 * 
 * @author Benoit
 */
public class CommunicationApiFactory {

    public ICommunicationApi createCommunicationApiV1() {
        return new CommunicationApiV1(new CommunicationService(new SocketFactory()));
    }

    public IConcurrentCommunicationApi createConcurrentCommunicationApiV1() {
        return new ConcurrentCommunicationApiV1(new ConcurrentCommunicationService());
    }

    public IKVCommunicationApi createKVCommunicationApiV1() {
        return new KVCommunicationApiV1(createCommunicationApiV1(), new KVMessageSerializer(),
                new KVMessageDeserializer());
    }
}
