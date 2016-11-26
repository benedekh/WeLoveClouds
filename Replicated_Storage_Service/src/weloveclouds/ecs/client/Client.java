package weloveclouds.ecs.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

import weloveclouds.cli.models.ParsedUserInput;
import weloveclouds.cli.utils.UserInputReader;
import weloveclouds.cli.utils.UserOutputWriter;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.ecs.models.commands.client.EcsClientCommandFactory;
import weloveclouds.ecs.utils.EcsClientUserInputParser;

/**
 * Created by Benoit on 2016-11-16.
 */
public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class);

    private EcsClientCommandFactory ecsCommandFactory;
    private InputStream inputStream;

    public Client(InputStream inputStream, EcsClientCommandFactory ecsCommandFactory) {
        this.inputStream = inputStream;
        this.ecsCommandFactory = ecsCommandFactory;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        try (UserInputReader inputReader = new UserInputReader(inputStream, new EcsClientUserInputParser());
             UserOutputWriter outputWriter = UserOutputWriter.getInstance()) {
            LOGGER.info("ECS client started.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    outputWriter.writePrefix();
                    ParsedUserInput userInput = inputReader.readAndParseUserInput();
                    ecsCommandFactory.createCommandFromUserInput(userInput).validate().execute();
                    LOGGER.info("Command executed.");
                } catch (ClientSideException | IllegalArgumentException | IOException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    LOGGER.error(ex);
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } catch (Throwable ex) {
            LOGGER.fatal(ex);
        } finally {
            LOGGER.info("Client stopped.");
        }
    }
}
