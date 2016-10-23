package weloveclouds.client.core;

import static weloveclouds.client.core.ClientState.LISTENING;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.UserInputConverter;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToConnectException;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.Request;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit
 */
public class Client {
  private ICommunicationApi communicationApi;
  private UserInputConverter<Request> userInputConverter;
  private BufferedReader inputReader;
  private ClientState state;
  private Logger clientLogger;

  @Inject
  public Client(ICommunicationApi communicationApi, UserInputConverter userInputConverter) {
    this.communicationApi = communicationApi;
    this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    this.userInputConverter = userInputConverter;
    /*initialize logger, There's a way to do this with an external config file but I haven't 
     * worked with it at all
     */
    this.clientLogger = Logger.getLogger(Client.class.getName());
    //this.clientLogger.addAppender(newAppender);
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
      case CONNECT:
        /**
         * @see Parser, which validates the data provided 
         */
        /*I need advice on getting the network info out of the string, should this processing
         * even be done here? We could do it in the UserInput factory i think.
         */
        String[] connectionInfo = apiRequest.getArgument().split(":");
        String ipString = connectionInfo[0];
        int port = Integer.parseInt(connectionInfo[1]);
        try {
          communicationApi.connectTo(new ServerConnectionInfo(InetAddress.getByName(ipString)
                                                                         ,port));
        } catch (UnableToConnectException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (UnknownHostException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
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
