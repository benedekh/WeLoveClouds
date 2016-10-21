package main.java.com.weloveclouds.client.utils;

import java.util.IllegalFormatException;

import main.java.com.weloveclouds.client.communication.models.Command;
import main.java.com.weloveclouds.client.communication.models.Request;
import main.java.com.weloveclouds.client.models.UserInput;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UserInputToApiRequestConverterV1 implements UserInputConverter<Request>{
    public Request convert(UserInput userInput){
        try {
            return new Request()
                    .withCommand(Command.valueOf(userInput.getCommand().toUpperCase()))
                    .withPayload(userInput.getPayload());
        }catch(IllegalArgumentException e){
            return null;
        }
    }
}
