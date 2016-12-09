package weloveclouds.server.commands.client;


import weloveclouds.commons.exceptions.ServerSideException;

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
