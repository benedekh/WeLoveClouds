package weloveclouds.client.models.commands;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-10-25.
 */
public interface AbstractCommand {

    void execute() throws ClientSideException;
}
