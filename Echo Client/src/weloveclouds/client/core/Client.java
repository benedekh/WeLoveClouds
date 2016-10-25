package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.client.models.Command;
import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.client.utils.LogManager;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.client.utils.UserInputReader;
import weloveclouds.client.utils.UserInputValidator;
import weloveclouds.client.utils.UserOutputWriter;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit, Benedek
 */
public class Client {

  private static final String HELP_MESSAGE = HelpMessageGenerator.generateHelpMessage();

  private ICommunicationApi communicationApi;
  private InputStream inputStream;
  private OutputStream outputStream;

  private Logger logger;

  @Inject
  public Client(ICommunicationApi communicationApi, OutputStream outputStream,
      InputStream inputStream) {
    this.communicationApi = communicationApi;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.logger = Logger.getLogger(this.getClass());
  }

  public void run() {
    try (UserInputReader inputReader = new UserInputReader(inputStream);
        UserOutputWriter outputWriter = new UserOutputWriter(outputStream);) {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          UserInput input = inputReader.readLine();
          Command command = input.getCommand();
          String argument = input.getArgument();

          switch (command) {
            case CONNECT:
              try {
                UserInputValidator.validateConnectArgument(argument);

                ServerConnectionInfo connectionInfo =
                    UserInputParser.extractConnectionInfoFromInput(argument);
                communicationApi.connectTo(connectionInfo);
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              } catch (UnableToConnectException e) {
                outputWriter.writeLine(e.getMessage());
              } finally {
                outputWriter.writePrefix();
              }
              break;
            case SEND:
              try {
                UserInputValidator.validateSendArgument(argument);

                communicationApi.send(input.getArgumentAsBytes());
                byte[] response = communicationApi.receive();
                outputWriter.writeLine(new String(response, StandardCharsets.US_ASCII));
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              } catch (UnableToSendRequestToServerException | ConnectionClosedException ex) {
                outputWriter.writeLine(ex.getMessage());
              } finally {
                outputWriter.writePrefix();
              }
              break;
            case DISCONNECT:
              try {
                UserInputValidator.validateDisconnectArgument(argument);

                communicationApi.disconnect();
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              } catch (UnableToDisconnectException ex) {
                outputWriter.writeLine(ex.getMessage());
              } finally {
                outputWriter.writePrefix();
              }
              break;
            case QUIT:
              try {
                UserInputValidator.validateQuitArgument(argument);

                if (communicationApi.isConnected()) {
                  try {
                    communicationApi.disconnect();
                  } catch (UnableToDisconnectException ex) {
                    outputWriter.writeLine(ex.getMessage());
                  }
                }
                outputWriter.writeLine("Program was shut down.");
                return;
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              }
              break;
            case HELP:
              try {
                UserInputValidator.validateHelpArgument(argument);

                outputWriter.writeLine(HELP_MESSAGE);
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              } finally {
                outputWriter.writePrefix();
              }
              break;
            case LOGLEVEL:
              try {
                UserInputValidator.validateLogLevelArgument(argument);

               Level level = Level.toLevel(argument);
                LogManager.getInstance().setLogLevel(level);
                outputWriter.writeLine(String.format("Current log level: %s", level));
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              } finally {
                outputWriter.writePrefix();
              }
              break;
            case DEFAULT:
              try {
                outputWriter.writeLine("Unknown command.");
                outputWriter.writeLine(HELP_MESSAGE);
              } catch (IllegalArgumentException ex) {
                outputWriter.writeLine(ex.getMessage());
              } finally {
                outputWriter.writePrefix();
              }
              break;
          }

        } catch (IOException ex) {
          logger.error("Error while reading input from the user or writing the output.");
        }
      }
    }
  }
}
