package weloveclouds.client.core;

import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import weloveclouds.client.models.UserInput;
import weloveclouds.client.utils.UserInputConverter;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.communication.api.CommunicationApi;
import weloveclouds.communication.exceptions.UnableToDisconnectException;
import weloveclouds.communication.exceptions.UnableToSendRequestToServerException;
import weloveclouds.communication.models.Request;

import static weloveclouds.client.core.ClientState.*;
import static weloveclouds.communication.models.Command.SEND;

/**
 * Created by Benoit on 2016-10-21.
 */
public class Client {
    private CommunicationApi communicationApi;
    private UserInputConverter<Request> userInputConverter;
    private BufferedReader inputReader;
    private ClientState state;

    @Inject
    public Client(CommunicationApi communicationApi, UserInputConverter userInputConverter){
        this.communicationApi = communicationApi;
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
        this.userInputConverter = userInputConverter;
    }

    public void run(){
        this.state = LISTENING;

        while(state == LISTENING){
            try {
                UserInput userInput = UserInputParser.parse(inputReader.readLine());
                Request apiRequest = userInputConverter.convert(userInput);

                if(apiRequest != null){
                    executeApiCall(apiRequest);
                }

            }catch(Exception e){

            }
        }
    }

    private void executeApiCall(Request apiRequest) throws UnableToSendRequestToServerException,
            UnableToDisconnectException{
        switch(apiRequest.getCommand()){
            case SEND:
                communicationApi.send(apiRequest);
                break;
            case DISCONNECT:
                communicationApi.disconnect();
                break;
        }
    }
}
