package app_kvEcs;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigInteger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.api.v1.KVEcsApiV1;
import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.core.ExternalConfigurationService;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
import weloveclouds.ecs.exceptions.authentication.InvalidAuthenticationInfosException;
import weloveclouds.ecs.models.commands.EcsCommandFactory;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.services.JshSecureShellService;
import weloveclouds.ecs.services.TaskService;
import weloveclouds.ecs.utils.ConfigurationFileParser;
import weloveclouds.hashing.models.Hash;
import weloveclouds.hashing.utils.HashingUtil;
import weloveclouds.kvstore.deserialization.KVAdminMessageDeserializer;
import weloveclouds.kvstore.serialization.KVAdminMessageSerializer;
import weloveclouds.server.utils.LogSetup;

public class ECSClient {
    private static Logger LOGGER = Logger.getLogger(ECSClient.class);

    public static void main(String[] args) {
        String logFile = "logs/ecs.log";
        try {
            new LogSetup(logFile, Level.OFF);
            ExternalConfigurationService ecs = new ExternalConfigurationService.Builder()
                    .taskService(new TaskService())
                    .CommunicationApiFactory(new CommunicationApiFactory())
                    .secureShellService(new JshSecureShellService())
                    .configurationFilePath(args[0])
                    .ecsRepositoryFactory(new EcsRepositoryFactory(new ConfigurationFileParser()))
                    .build();

            IKVEcsApi externalConfigurationServiceApi = new KVEcsApiV1(ecs);
            EcsCommandFactory ecsCommandFactory = new EcsCommandFactory(externalConfigurationServiceApi);
            Client ecsClient = new Client(System.in, ecsCommandFactory);
            ecsClient.run();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        } catch (InvalidAuthenticationInfosException ex) {
            LOGGER.error("A bad authentication configuration file prevent the system from " +
                    "starting. A username and a password or privatekey should be provided.");
        } catch (ServiceBootstrapException ex) {
            LOGGER.fatal(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.fatal("No ecs configuration file path provided.");
        }
    }
}