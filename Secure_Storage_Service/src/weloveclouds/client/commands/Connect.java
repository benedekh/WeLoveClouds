package weloveclouds.client.commands;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.commands.utils.ArgumentsValidator;
import weloveclouds.commons.cli.utils.AbstractUserInputParser;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Connect command which means a connection to the server.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Connect extends AbstractCommunicationApiCommand {

    private static final Logger LOGGER = Logger.getLogger(Connect.class);
    private ServerConnectionInfo remoteServer;

    /**
     * @param arguments contains the IP address, and the port
     * @param communicationApi a reference to the communication module
     * @throws UnknownHostException see
     *         {@link AbstractUserInputParser#extractConnectionInfoFrom(String[])}
     */
    public Connect(String[] arguments, ICommunicationApi communicationApi)
            throws UnknownHostException {
        super(arguments, communicationApi);
        this.remoteServer = AbstractUserInputParser.extractConnectionInfoFrom(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing connect command.");
            communicationApi.connectTo(remoteServer);
            userOutputWriter.writeLine("Connected to server.");
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info("Connect command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateConnectArguments(arguments, remoteServer);
        return this;
    }
}
