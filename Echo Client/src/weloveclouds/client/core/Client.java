package weloveclouds.client.core;

import static weloveclouds.client.core.ClientState.LISTENING;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.UserInputConverter;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.Request;

/**
 * @author Benoit
 */
public class Client {
  private ICommunicationApi communicationApi;
  private UserInputConverter<Request> userInputConverter;
  private BufferedReader inputReader;
  private ClientState state;
  private static Logger clientLogger;

  @Inject
  public Client(ICommunicationApi communicationApi, UserInputConverter userInputConverter) {
    this.communicationApi = communicationApi;
    this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    this.userInputConverter = userInputConverter;
    this.clientLogger = Logger.getLogger(Client.class.getName());
  }

  public void run() {
    this.state = LISTENING;

    while (state == LISTENING) {
      System.out.print("EchoClient> ");
      try {
        UserInput userInput = UserInputParser.parse(inputReader.readLine());
        Request apiRequest = userInputConverter.convert(userInput);

        if (apiRequest != null) {
          executeApiCall(apiRequest);
        }

      } catch (Exception e) {

      }
    }
  }

  private void executeApiCall(Request apiRequest) throws UnableToSendRequestToServerException,
      UnableToDisconnectException, ConnectionClosedException {
    switch (apiRequest.getCommand()) {
      case SEND:
        communicationApi.send(apiRequest.argumentAsBytes());
        communicationApi.receive();
        break;
      case DISCONNECT:
        communicationApi.disconnect();
        break;
    }
  }
}
