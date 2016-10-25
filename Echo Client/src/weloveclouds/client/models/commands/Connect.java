package weloveclouds.client.models.commands;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.UserInputParser;
import weloveclouds.client.utils.UserOutputWriter;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Created by Benoit on 2016-10-25.
 */
public class Connect extends AbstractCommunicationApiCommand {
    private ServerConnectionInfo remoteServer;

    public Connect(String[] arguments, ICommunicationApi communicationApi) throws UnknownHostException {
        super(arguments, communicationApi);
        this.remoteServer = UserInputParser.extractConnectionInfoFrom(arguments);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            communicationApi.connectTo(remoteServer);
            userOutputWriter.writeLine(new String(communicationApi.receive(),
                    StandardCharsets.US_ASCII));
        }catch(IOException ex){
            throw new ClientSideException(ex.getMessage(), ex);
        }
    }

    @Override
    public ICommand validate() throws InvalidParameterException {
        ArgumentsValidator.validateConnectArguments(arguments, remoteServer);
        return this;
    }
}
