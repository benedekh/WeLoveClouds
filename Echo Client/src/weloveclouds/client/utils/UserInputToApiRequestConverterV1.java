package weloveclouds.client.utils;

import java.util.IllegalFormatException;

import weloveclouds.client.models.UserInput;
import weloveclouds.communication.models.Command;
import weloveclouds.communication.models.Request;

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
