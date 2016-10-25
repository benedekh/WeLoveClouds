package weloveclouds.client.models.commands;

import weloveclouds.client.utils.UserOutputWriter;

/**
 * Created by Benoit on 2016-10-25.
 */
public abstract class AbstractCommand implements ICommand{
    protected String[] arguments;
    protected UserOutputWriter userOutputWriter = UserOutputWriter.getInstance();

    public AbstractCommand(String[] arguments){
        this.arguments = arguments;
    }
}
