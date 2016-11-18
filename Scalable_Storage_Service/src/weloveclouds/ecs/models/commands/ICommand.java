package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.models.commands.IValidatable;

/**
 * Represents an object (command) that can be executed.
 *
 * @author Benoit, adapted to by hb
 */
public interface ICommand extends IValidatable {

    /**
     * Executes the respective command.
     *
     * @throws ClientSideException if any error occurs
     */
    void execute() throws Exception;
    //TODO: write custom exception
}