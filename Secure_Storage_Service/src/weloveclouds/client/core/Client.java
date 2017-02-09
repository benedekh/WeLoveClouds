package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import weloveclouds.client.commands.CommandFactory;
import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.commons.cli.utils.UserInputReader;
import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.exceptions.ClientSideException;

/**
 * Processes various commands that are received from the user. For details on the the commands see
 * {@link CommandFactory}.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class);

    private InputStream inputStream;
    private CommandFactory commandFactory;

    /**
     * @param inputStream from which it receives command from the user
     * @param commandFactory that processes (validate and execute) the various commands
     */
    public Client(InputStream inputStream, CommandFactory commandFactory) {
        this.inputStream = inputStream;
        this.commandFactory = commandFactory;
    }

    /**
     * Reads commands with arguments from the user via the {@link #inputStream}. After, it forwards
     * the respective command to the {@link #commandFactory} that will validate and execute it.
     */
    @SuppressWarnings("unchecked")
    public void run() {
        try (UserInputReader inputReader =
                new UserInputReader(inputStream, new ClientUserInputParser());
                UserOutputWriter outputWriter = UserOutputWriter.getInstance()) {
            LOGGER.info("Client started.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    outputWriter.writePrefix();
                    ParsedUserInput userInput = inputReader.readAndParseUserInput();
                    commandFactory.createCommandFromUserInput(userInput).validate().execute();
                    LOGGER.info("Command executed.");
                } catch (IOException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    LOGGER.error(ex);
                } catch (ClientSideException | IllegalArgumentException ex) {
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
