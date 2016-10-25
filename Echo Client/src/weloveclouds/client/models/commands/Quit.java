package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Quit extends AbstractCommand {

    public Quit(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        System.exit(0);
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateQuitArguments(arguments);
        return this;
    }
}
