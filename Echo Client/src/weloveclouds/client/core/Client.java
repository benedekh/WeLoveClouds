package weloveclouds.client.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;

/**
 * @author Benoit, Benedek
 */
public class Client {
  private ICommunicationApi communicationApi;
  private InputStream inputStream;
  private OutputStream outputStream;

  private Logger logger;

  @Inject
  public Client(ICommunicationApi communicationApi, InputStream inputStream,
      OutputStream outputStream) {
    this.communicationApi = communicationApi;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.logger = Logger.getLogger(this.getClass());
  }

  public void run() {
    BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
    BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

    try {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          UserInput input = UserInputParser.parse(inputReader.readLine());
          switch (input.getCommand()) {
            // TODO validate input
            case CONNECT:
              // TODO get IP and port
              break;
            case SEND:
              try {
                communicationApi.send(input.getArgumentAsBytes());
                byte[] response = communicationApi.receive();
                outputWriter.write(new String(response, StandardCharsets.US_ASCII));
              } catch (UnableToSendRequestToServerException | ConnectionClosedException ex) {
                logger.error(ex.getMessage());
              }
              break;
            case DISCONNECT:
              try {
                communicationApi.disconnect();
              } catch (UnableToDisconnectException ex) {
                logger.error(ex.getMessage());
              }
              break;
            case QUIT:
              if (communicationApi.isConnected()) {
                try {
                  communicationApi.disconnect();
                } catch (UnableToDisconnectException ex) {
                  logger.error(ex.getMessage());
                }
              }
              outputWriter.write("Program was shut down.");
              return;
            case HELP:
              // TODO print help text
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
          logger.error("Error while reading input from the user.");
        }
      }
    } finally {
      try {
        inputReader.close();
      } catch (IOException ex) {
        // suppress exception
      }
      try {
        outputWriter.close();
      } catch (IOException ex) {
        // suppress exception
      }
    }
  }
}
