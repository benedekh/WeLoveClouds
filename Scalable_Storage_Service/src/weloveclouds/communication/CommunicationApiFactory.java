package weloveclouds.communication;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.api.v1.CommunicationApiV1;
import weloveclouds.communication.api.v1.ConcurrentCommunicationApiV1;
import weloveclouds.server.api.IKVCommunicationApi;
import weloveclouds.server.api.v1.KVCommunicationApiV1;
import weloveclouds.communication.services.CommunicationService;
import weloveclouds.communication.services.ConcurrentCommunicationService;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;

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
}
