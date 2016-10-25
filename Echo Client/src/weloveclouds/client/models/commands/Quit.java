package weloveclouds.client.models.commands;

import java.io.IOException;
import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Quit extends AbstractCommand {
    private static final String APPLICATION_EXITED_MESSAGE = "Application exit !";

    public Quit(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            userOutputWriter.writeLine(APPLICATION_EXITED_MESSAGE);
            System.exit(0);
        }catch(IOException e){
            throw new ClientSideException(e.getMessage(), e);
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }
}
