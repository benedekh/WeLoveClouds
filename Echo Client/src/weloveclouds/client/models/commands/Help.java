package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.HelpMessageGenerator;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Help extends AbstractCommand {

    public Help(String[] arguments) {
        super(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        System.out.println(HelpMessageGenerator.generateHelpMessage());
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateHelpArguments(arguments);
        return this;
    }
}
