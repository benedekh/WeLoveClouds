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
import weloveclouds.commons.kvstore.deserialization.helper.HashRangeDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.ServerConnectionInfoDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.HashRangeSerializer;
import weloveclouds.commons.kvstore.serialization.helper.HashSerializer;
import weloveclouds.commons.kvstore.serialization.helper.ServerConnectionInfoSerializer;
import weloveclouds.commons.serialization.NodeHealthInfosSerializer;
import weloveclouds.commons.serialization.RingTopologySerializer;
import weloveclouds.commons.serialization.StorageNodeSerializer;
import weloveclouds.commons.serialization.deserialization.NodeHealtInfosDeserializer;
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