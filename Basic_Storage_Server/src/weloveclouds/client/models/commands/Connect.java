package weloveclouds.client.models.commands;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.communication.api.v1.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Connect command which means a connection to the server.
 * 
 * @author Benoit, Benedek, Hunton
 */
public class Connect extends AbstractCommunicationApiCommand {
    private ServerConnectionInfo remoteServer;
    private Logger logger;

    /**
     * @param arguments contains the IP address (0. element of the array), and the port (1st element
     *        of the array)
     * @param communicationApi a reference to the communication module
     * @throws UnknownHostException see {@link UserInputParser#extractConnectionInfoFrom(String[])}
     */
    public Connect(String[] arguments, ICommunicationApi communicationApi)
            throws UnknownHostException {
        super(arguments, communicationApi);
        this.remoteServer = UserInputParser.extractConnectionInfoFrom(arguments);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing connect command.");
            communicationApi.connectTo(remoteServer);
        } finally {
            logger.info("Connect command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateConnectArguments(arguments, remoteServer);
        return this;
    }
}
