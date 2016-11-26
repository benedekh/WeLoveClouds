package app_kvEcs;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.api.v1.KVEcsApiV1;
import weloveclouds.ecs.client.Client;
import weloveclouds.ecs.core.ExternalConfigurationService;
import weloveclouds.ecs.exceptions.ServiceBootstrapException;
import weloveclouds.ecs.exceptions.authentication.InvalidAuthenticationInfosException;
import weloveclouds.ecs.models.commands.client.EcsClientCommandFactory;
import weloveclouds.ecs.models.commands.internal.EcsInternalCommandFactory;
import weloveclouds.ecs.models.repository.EcsRepositoryFactory;
import weloveclouds.ecs.models.ssh.SecureShellServiceFactory;
import weloveclouds.ecs.services.TaskService;
import weloveclouds.ecs.utils.ConfigurationFileParser;
import weloveclouds.server.utils.LogSetup;

public class ECSClient {
    private static Logger LOGGER = Logger.getLogger(ECSClient.class);
    private static UserOutputWriter userOutput = UserOutputWriter.getInstance();

    public static void main(String[] args) throws Exception {
        String logFile = "logs/ecs.log";
        try {
            new LogSetup(logFile, Level.OFF);
            EcsInternalCommandFactory ecsInternalCommandFactory = new EcsInternalCommandFactory
                    (new CommunicationApiFactory(), new SecureShellServiceFactory());

            ExternalConfigurationService ecs = new ExternalConfigurationService.Builder()
                    .taskService(new TaskService())
                    .ecsInternalCommandFactory(ecsInternalCommandFactory)
                    .configurationFilePath(args[0])
                    .ecsRepositoryFactory(new EcsRepositoryFactory(new ConfigurationFileParser()))
                    .build();

            IKVEcsApi externalConfigurationServiceApi = new KVEcsApiV1(ecs);
            EcsClientCommandFactory ecsCommandFactory = new EcsClientCommandFactory(externalConfigurationServiceApi);
            Client ecsClient = new Client(System.in, ecsCommandFactory);
            ecsClient.run();

        } catch (IOException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.error(ex.getMessage());
        } catch (InvalidAuthenticationInfosException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.error("A bad authentication configuration file prevent the system from " +
                    "starting. A username and a password or privatekey should be provided.");
        } catch (ServiceBootstrapException ex) {
            userOutput.writeLine(ex.getMessage() + ex.getCause());
            LOGGER.fatal(ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            userOutput.writeLine("No ecs configuration file path provided.");
            LOGGER.fatal("No ecs configuration file path provided.");
        }
    }
}