package weloveclouds.server.core;

import java.io.IOException;

import org.apache.log4j.Logger;

import weloveclouds.commons.networking.Server;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVTransferMessageDeserializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.KVTransferMessageSerializer;
import weloveclouds.server.models.requests.kvclient.IKVClientRequest;
import weloveclouds.server.models.requests.kvclient.KVClientRequestFactory;
import weloveclouds.server.models.requests.kvecs.IKVECSRequest;
import weloveclouds.server.models.requests.kvecs.KVECSRequestFactory;
import weloveclouds.server.models.requests.kvserver.IKVServerRequest;
import weloveclouds.server.models.requests.kvserver.KVServerRequestFactory;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * Factory class which creates different {@link Server} instances, depending on the processable
 * message type.
 * 
 * @author Benedek
 */
public class ServerFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerFactory.class);

    /**
     * Create a Server which serves KVClient requests.
     * 
     * @param port where the server shall be started
     * @param dataAccessService that will be the data access service
     * @throws IOException if the server cannot be created on the referred port
     */
    public Server<KVMessage, IKVClientRequest> createServerForKVClientRequests(int port,
            IDataAccessService dataAccessService) throws IOException {
        LOGGER.debug("Creating Server for KVClient requests.");
        return new Server.Builder<KVMessage, IKVClientRequest>().port(port)
                .serverSocketFactory(new ServerSocketFactory())
                .requestFactory(new KVClientRequestFactory(dataAccessService))
                .communicationApiFactory(new CommunicationApiFactory())
                .messageSerializer(new KVMessageSerializer())
                .messageDeserializer(new KVMessageDeserializer()).build();
    }


    /**
     * Create a Server which serves KVServer requests.
     * 
     * @param port where the server shall be started
     * @param dataAccessService that will be the data access service
     * @throws IOException if the server cannot be created on the referred port
     */
    public Server<KVTransferMessage, IKVServerRequest> createServerForKVServerRequests(int port,
            IMovableDataAccessService dataAccessService) throws IOException {
        LOGGER.debug("Creating Server for KVServer requests.");
        return new Server.Builder<KVTransferMessage, IKVServerRequest>().port(port)
                .serverSocketFactory(new ServerSocketFactory())
                .requestFactory(new KVServerRequestFactory(dataAccessService))
                .communicationApiFactory(new CommunicationApiFactory())
                .messageSerializer(new KVTransferMessageSerializer())
                .messageDeserializer(new KVTransferMessageDeserializer()).build();
    }

    /**
     * Create a Server which serves KVECS requests.
     * 
     * @param port where the server shall be started
     * @param dataAccessService that will be the data access service
     * @throws IOException if the server cannot be created on the referred port
     */
    public Server<KVAdminMessage, IKVECSRequest> createServerForKVECSRequests(int port,
            IMovableDataAccessService dataAccessService) throws IOException {
        CommunicationApiFactory communicationApiFactory = new CommunicationApiFactory();
        LOGGER.debug("Creating Server for KVECS requests.");
        return new Server.Builder<KVAdminMessage, IKVECSRequest>().port(port)
                .serverSocketFactory(new ServerSocketFactory())
                .requestFactory(new KVECSRequestFactory(dataAccessService, communicationApiFactory,
                        new KVTransferMessageSerializer(), new KVTransferMessageDeserializer()))
                .communicationApiFactory(communicationApiFactory)
                .messageSerializer(new KVAdminMessageSerializer())
                .messageDeserializer(new KVAdminMessageDeserializer()).build();
    }
}
