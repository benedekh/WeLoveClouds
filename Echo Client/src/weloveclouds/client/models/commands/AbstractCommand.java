package weloveclouds.client.models.commands;

/**
 * Created by Benoit on 2016-10-25.
 */
public abstract class AbstractCommand implements ICommand{
    protected String[] arguments;

    public AbstractCommand(String[] arguments){
        this.arguments = arguments;
    }
}
