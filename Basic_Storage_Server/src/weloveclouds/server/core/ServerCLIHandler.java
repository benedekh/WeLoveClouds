package weloveclouds.server.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import weloveclouds.cli.utils.UserInputReader;
import weloveclouds.cli.utils.UserOutputWriter;
import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.server.models.commands.ServerCommandFactory;
import weloveclouds.server.models.exceptions.ServerSideException;

public class ServerCLIHandler {
    private InputStream inputStream;
    private ServerCommandFactory commandFactory;

    private Logger logger;

    /**
     * @param inputStream from which it receives command from the user
     * @param commandFactory that processes (validate and execute) the various commands
     */
    public ServerCLIHandler(InputStream inputStream, ServerCommandFactory commandFactory) {
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
            UserOutputWriter.setPrefix("Server> ");
            logger.info("Server started.");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    outputWriter.writePrefix();
                    ParsedUserInput userInput = inputReader.readAndParseUserInput();
                    commandFactory.createCommandFromUserInput(userInput).validate().execute();
                    logger.info("Command executed.");
                } catch (IOException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    logger.error(ex);
                } catch (ServerSideException | IllegalArgumentException ex) {
                    outputWriter.writeLine(ex.getMessage());
                    logger.error(ex);
                }
            }
        } catch (IOException ex) {
            logger.error(ex);
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
        } finally {
            logger.info("Server stopped.");
        }
    }
}
