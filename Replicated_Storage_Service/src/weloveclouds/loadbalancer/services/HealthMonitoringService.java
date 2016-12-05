package weloveclouds.loadbalancer.services;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

import weloveclouds.commons.networking.AbstractServer;
import weloveclouds.commons.networking.ServerSocketFactory;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.serialization.models.SerializedMessage;

import static weloveclouds.commons.status.ServerStatus.RUNNING;

/**
 * Created by Benoit on 2016-12-05.
 */
public class HealthMonitoringService {
    private static final Logger LOGGER = Logger.getLogger(HealthMonitoringService.class);

    private DistributedSystemAccessService distributedSystemAccessService;

    /**
     * @param serverSocketFactory to create the server socket on the referred port
     * @param port                on which the server will listen
     * @throws IOException {@link ServerSocketFactory#createServerSocketFromPort(int)}}
     */
    public HealthMonitoringService(ServerSocketFactory serverSocketFactory, int port,
                                   DistributedSystemAccessService distributedSystemAccessService,
                                   IMessageDeserializer<KVAdminMessage, SerializedMessage> messageDeserializer)
            throws IOException {
        this.distributedSystemAccessService = distributedSystemAccessService;
    }
}
