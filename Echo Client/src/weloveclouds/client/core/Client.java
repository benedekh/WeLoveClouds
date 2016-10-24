package weloveclouds.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                UserInputValidator.validateConnectArgument(argument);

                // TODO use regex and scanner to get the IP address and port
                String[] argumentParts = argument.split("\\s+");
                String ip = argumentParts[0];
                int port = Integer.parseInt(argumentParts[1]);
                ServerConnectionInfo connectionInfo =
                    new ServerConnectionInfo.ServerConnectionInfoBuilder().ipAddress(ip).port(port)
                        .build();

                communicationApi.connectTo(connectionInfo);
              } catch (IllegalArgumentException ex) {
                outputWriter.write(ex.getMessage());
              } catch (UnableToConnectException e) {
                outputWriter.write(e.getMessage());
              }
              break;
            case SEND:
              try {
                UserInputValidator.validateSendArgument(argument);

                communicationApi.send(input.getArgumentAsBytes());
                byte[] response = communicationApi.receive();
                outputWriter.write(new String(response, StandardCharsets.US_ASCII));
              } catch (IllegalArgumentException ex) {
                outputWriter.write(ex.getMessage());
              } catch (UnableToSendRequestToServerException | ConnectionClosedException ex) {
                outputWriter.write(ex.getMessage());
              }
              break;
            case DISCONNECT:
              try {
                UserInputValidator.validateDisconnectArgument(argument);
                communicationApi.disconnect();
              } catch (IllegalArgumentException ex) {
                outputWriter.write(ex.getMessage());
              } catch (UnableToDisconnectException ex) {
                outputWriter.write(ex.getMessage());
              }
              break;
            case QUIT:
              try {
                UserInputValidator.validateQuitArgument(argument);

                if (communicationApi.isConnected()) {
                  try {
                    communicationApi.disconnect();
                  } catch (UnableToDisconnectException ex) {
                    outputWriter.write(ex.getMessage());
                  }
                }
                outputWriter.write("Program was shut down.");
                return;
              } catch (IllegalArgumentException ex) {
                outputWriter.write(ex.getMessage());
              }
              break;
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
