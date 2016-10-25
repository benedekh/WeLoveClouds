package weloveclouds.client.models.commands;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public interface ICommand extends IValidatable{

    void execute() throws ClientSideException;
}
