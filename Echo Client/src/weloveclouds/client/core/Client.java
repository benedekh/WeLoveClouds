package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.UserInputReader;
import weloveclouds.client.utils.UserOutputWriter;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * @author Benoit, Benedek
 */
public class Client {

    private InputStream inputStream;
    private CommandFactory commandFactory;

    private Logger logger;

    public Client(InputStream inputStream, CommandFactory commandFactory) {
        this.inputStream = inputStream;
        this.commandFactory = commandFactory;
        this.logger = Logger.getLogger(getClass());
    }

    public void run() {
        try (UserInputReader inputReader = new UserInputReader(inputStream);
                UserOutputWriter outputWriter = UserOutputWriter.getInstance()) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    logger.info("Client started.");
                    outputWriter.writePrefix();
                    ParsedUserInput userInput = inputReader.readAndParseUserInput();
                    commandFactory.createCommandFromUserInput(userInput).validate().execute();
                    logger.info("Command executed.");
                } catch (IOException ex) {
                    outputWriter.writeLine(
                            "Error while reading from the user or writing the user output.");
                    logger.error(ex.getMessage(), ex);
                } catch (ClientSideException | IllegalArgumentException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    logger.error(ex.getMessage(), ex);
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
        } finally {
            logger.info("Client stopped.");
        }
    }
}
