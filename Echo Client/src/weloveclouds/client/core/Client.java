package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.models.commands.CommandFactory;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.client.utils.LogManager;
import weloveclouds.client.utils.UserInputReader;
import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.UserOutputWriter;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * @author Benoit, Benedek
 */
public class Client {

    private static final String HELP_MESSAGE = HelpMessageGenerator.generateHelpMessage();

    private ICommunicationApi communicationApi;
    private InputStream inputStream;
    private OutputStream outputStream;
    private CommandFactory commandFactory;

    private Logger logger;

    @Inject
    public Client(ICommunicationApi communicationApi, OutputStream outputStream,
                  InputStream inputStream, CommandFactory commandFactory) {
        this.communicationApi = communicationApi;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.logger = Logger.getLogger(this.getClass());
        this.commandFactory = commandFactory;
    }

    public void run() {
        try {
            try (UserInputReader inputReader = new UserInputReader(inputStream);
                 UserOutputWriter outputWriter = new UserOutputWriter(outputStream);) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        UserInput userInput = inputReader.readLine();
                        commandFactory.createCommandFromUserInput(userInput).validate().execute();
                    } catch (IOException ex) {
                        logger.error("Error while reading input from the user.");
                    } catch (ClientSideException | IllegalArgumentException ex) {
                        outputWriter.writeLine(ex.getMessage());
                    }
                }
            }
        }catch(IOException ex){
            logger.error("Error while writing the output.");
        }
    }
}
