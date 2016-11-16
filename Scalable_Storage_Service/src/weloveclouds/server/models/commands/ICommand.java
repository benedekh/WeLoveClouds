package weloveclouds.server.models.commands;

import weloveclouds.server.exceptions.ServerSideException;

/**
 * Represents an object (command) that can be executed.
 *
 * @author Benoit
 */
public interface ICommand extends IValidatable {

    /**
     * Executes the respective command.
     *
     * @throws ServerSideException if any error occurs
     */
    void execute() throws ServerSideException;
}
