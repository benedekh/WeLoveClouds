package main.java.com.weloveclouds.client.core;

import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import main.java.com.weloveclouds.client.communication.api.CommunicationApi;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToDisconnectException;
import main.java.com.weloveclouds.client.communication.exceptions.UnableToSendRequestToServerException;
import main.java.com.weloveclouds.client.communication.models.Request;
import main.java.com.weloveclouds.client.models.UserInput;
import main.java.com.weloveclouds.client.utils.UserInputConverter;
import main.java.com.weloveclouds.client.utils.UserInputParser;

import static main.java.com.weloveclouds.client.communication.models.Command.SEND;
import static main.java.com.weloveclouds.client.core.ClientState.*;

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
