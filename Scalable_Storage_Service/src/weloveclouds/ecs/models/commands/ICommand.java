package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.models.commands.IValidatable;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * Represents an object (command) that can be executed.
 *
 * @author Benoit, copied here by hb
 */
public interface ICommand extends IValidatable {

    /**
     * Executes the respective command.
     *
     * @throws ClientSideException if any error occurs
     */
    void execute() throws ClientSideException;
}