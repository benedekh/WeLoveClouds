package weloveclouds.client.models.commands;

import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Represents an object (command) that can be executed.
 *
 * @author Benoit
 */
public interface ICommand extends IValidatable {

    /**
     * Executes the respective command.
     *
     * @throws ClientSideException if any error occurs
     */
    void execute() throws ClientSideException;
}
