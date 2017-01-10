package weloveclouds.server.client.commands;


import weloveclouds.commons.exceptions.ServerSideException;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * Represents an object (command) that can be executed.
 *
 * @author Benoit
 */
public interface ICommand extends IValidatable<ICommand> {

    /**
     * Executes the respective command.
     *
     * @throws ServerSideException if any error occurs
     */
    void execute() throws ServerSideException;
}
