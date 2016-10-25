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
        this.logger = Logger.getLogger(this.getClass());
        this.commandFactory = commandFactory;
    }

    public void run() {
        try (UserInputReader inputReader = new UserInputReader(inputStream);
             UserOutputWriter outputWriter = UserOutputWriter.getInstance()) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    outputWriter.writePrefix();
                    ParsedUserInput userInput = inputReader.readAndParseUserInput();
                    commandFactory.createCommandFromUserInput(userInput).validate().execute();
                } catch (IOException ex) {
                    logger.error("Error while reading input from the user.");
                } catch (ClientSideException | IllegalArgumentException ex) {
                    outputWriter.writeLine(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            logger.error("Error while writing the output.");
        }
    }
}
