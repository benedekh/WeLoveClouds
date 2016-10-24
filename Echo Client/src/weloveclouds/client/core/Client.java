package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.client.models.Command;
import weloveclouds.client.models.UserInput;
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
                boolean isValid = UserInputValidator.isConnectArgumentValid(argument);
                if (!isValid) {
                  outputWriter.write(
                      "Command is invalid. Either not enough parameters (IP address + port) are provided or there are some additional arguments which cannot be interpreted.");
                  break;
                }

                String[] argumentParts = argument.split("\\s+");
                String ip = argumentParts[0];
                int port = Integer.parseInt(argumentParts[1]);
                ServerConnectionInfo connectionInfo =
                    new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ip).port(port)
                        .build();

                communicationApi.connectTo(connectionInfo);
              } catch (UnknownHostException ex) {
                outputWriter.write("IP address is invalid.");
              } catch (NumberFormatException ex) {
                outputWriter.write("Port is invalid.");
              } catch (UnableToConnectException e) {
                outputWriter.write(e.getMessage());
              }
              break;
            case SEND:
              try {
                boolean isValid = UserInputValidator.isSendArgumentValid(argument);
                if (!isValid) {
                  outputWriter.write("Command is invalid. Message cannot be empty (null).");
                  break;
                }

                communicationApi.send(input.getArgumentAsBytes());
                byte[] response = communicationApi.receive();
                outputWriter.write(new String(response, StandardCharsets.US_ASCII));
              } catch (UnableToSendRequestToServerException | ConnectionClosedException ex) {
                outputWriter.write(ex.getMessage());
              }
              break;
            case DISCONNECT:
              try {
                boolean isValid = UserInputValidator.isDisconnectArgumentValid(argument);
                if (!isValid) {
                  outputWriter.write("Command is invalid. It does not interpret any argument.");
                  break;
                }
                communicationApi.disconnect();
              } catch (UnableToDisconnectException ex) {
                outputWriter.write(ex.getMessage());
              }
              break;
            case QUIT:
              boolean isValid = UserInputValidator.isQuitArgumentValid(argument);
              if (!isValid) {
                outputWriter.write("Command is invalid. It does not interpret any argument.");
                break;
              }

              if (communicationApi.isConnected()) {
                try {
                  communicationApi.disconnect();
                } catch (UnableToDisconnectException ex) {
                  outputWriter.write(ex.getMessage());
                }
              }
              outputWriter.write("Program was shut down.");
              return;
            case HELP:
              // TODO print help text (extract to a method)
              break;
            case LOGLEVEL:
              // TODO set log level and print current log status
              // TODO LoggerManager which sets all loggers log level and log output!
              // TODO see M1 slide 24
              break;
            case DEFAULT:
              outputWriter.write("Unknown command.");
              // TODO print help text
              break;
          }

        } catch (IOException ex) {
          logger.error("Error while reading input from the user or writing the output.");
        }
      }
    }
  }
}
