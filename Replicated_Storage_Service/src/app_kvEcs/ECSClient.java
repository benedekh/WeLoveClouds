package app_kvEcs;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.jcraft.jsch.HASH;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.RingTopologySerializer;
import weloveclouds.commons.serialization.StorageNodeSerializer;
import weloveclouds.commons.serialization.deserialization.RingTopologyDeserializer;
import weloveclouds.commons.serialization.deserialization.StorageNodeDeserializer;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.contexts.EcsExecutionContext;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.topology.RingTopology;
import weloveclouds.ecs.modules.client.EcsClientModule;
import weloveclouds.server.utils.LogSetup;

public class ECSClient {
    private static Logger LOGGER = Logger.getLogger(ECSClient.class);
    private static UserOutputWriter userOutput = UserOutputWriter.getInstance();
    private static final String LOG_FILE = "logs/ecs.log";

    public static void main(String[] args) throws Exception {
        try {
            new LogSetup(LOG_FILE, Level.OFF);
            ExecutionContext.setExecutionEnvironmentSystemPropertiesFromArgs(args);
            EcsExecutionContext.setConfigurationFilePath(args[0]);

            List<StorageNode> storageNodes = new ArrayList<>();
            HashRange hashRange = new HashRange.Builder()
                    .begin(new Hash(Hash.MIN_VALUE.getBytes()))
                    .end(new Hash(Hash.MIN_VALUE.getBytes()))
                    .build();

            storageNodes.add(new StorageNode.Builder()
                    .id("name1")
                    .serverConnectionInfo(new ServerConnectionInfo.Builder()
                            .ipAddress("100.100.100.1")
                            .port(1000).build())
                    .hashRange(hashRange)
                    .replicas(Arrays.asList(new StorageNode.Builder().build()))
                    .childHashranges(Arrays.asList(hashRange))
                    .build());
            storageNodes.add(new StorageNode.Builder()
                    .id("name2")
                    .serverConnectionInfo(new ServerConnectionInfo.Builder()
                            .ipAddress("100.100.100.2")
                            .port(1000).build())
                    .hashRange(hashRange)
                    .replicas(Arrays.asList(new StorageNode.Builder().build()))
                    .childHashranges(Arrays.asList(hashRange))
                    .build());
            storageNodes.add(new StorageNode.Builder()
                    .id("name3")
                    .serverConnectionInfo(new ServerConnectionInfo.Builder()
                            .ipAddress("100.100.100.3")
                            .port(1000).build())
                    .hashRange(hashRange)
                    .replicas(Arrays.asList(new StorageNode.Builder().build()))
                    .childHashranges(Arrays.asList(hashRange))
                    .build());
            RingTopology<StorageNode> topology = new RingTopology<>(storageNodes);
            String serializedTopology = new RingTopologySerializer<StorageNode>(new
                    StorageNodeSerializer(new ServerConnectionInfoSerializer(), new
                    HashSerializer(), new HashRangeSerializer(), new NodeHealthInfosSerializer
                    (new ServerConnectionInfoSerializer())))
                    .serialize(topology);

            RingTopology<StorageNode> deserializedTopo = new RingTopologyDeserializer<StorageNode>(new
                    StorageNodeDeserializer(null)).deserialize
                    (serializedTopology);
            Injector injector = Guice.createInjector(new EcsClientModule());
            Client ecsClient = injector.getInstance(Client.class);
            ecsClient.run();

        } catch (IOException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.error(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            userOutput.writeLine("No ecs configuration file path provided.");
            LOGGER.fatal("No ecs configuration file path provided.");
        }
    }
}