package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.commons.cli.utils.UserInputReader;
import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * The echo client itself. It processes (read, validate, execute) various commands that are received
 * from the user. For details on the the commands see {@link CommandFactory}.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Client {

    private InputStream inputStream;
    private CommandFactory commandFactory;

    private Logger logger;

    /**
     * @param inputStream from which it receives command from the user
     * @param commandFactory that processes (validate and execute) the various commands
     */
    public Client(InputStream inputStream, CommandFactory commandFactory) {
        this.inputStream = inputStream;
        this.commandFactory = commandFactory;
        this.logger = Logger.getLogger(getClass());
    }

    /**
     * Reads commands with arguments from the user via the {@link #inputStream}. After, it forwards
     * the respective command to the {@link #commandFactory} that will validate and execute it.
     */
    public void run() {
        try (UserInputReader inputReader = new UserInputReader(inputStream);
                UserOutputWriter outputWriter = UserOutputWriter.getInstance()) {
            logger.info("Client started.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    outputWriter.writePrefix();
                    ParsedUserInput userInput = inputReader.readAndParseUserInput();
                    commandFactory.createCommandFromUserInput(userInput).validate().execute();
                    logger.info("Command executed.");
                } catch (IOException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    logger.error(ex);
                } catch (ClientSideException | IllegalArgumentException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    logger.error(ex);
                }
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Throwable ex) {
            logger.fatal(ex);
        } finally {
            logger.info("Client stopped.");
        }
    }
}
