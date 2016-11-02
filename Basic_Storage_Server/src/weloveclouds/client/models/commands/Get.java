package weloveclouds.client.models.commands;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.kvstore.models.IKVMessage;

public class Get extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private Logger logger;

    public Get(String[] arguments, IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing get command.");

            IKVMessage response = communicationApi.get(arguments[KEY_INDEX]);
            logger.debug(response.toString());

            switch (response.getStatus()) {
                case GET_SUCCESS:
                case GET_ERROR:
                    userOutputWriter.writeLine(response.getValue());
                    break;
                default:
                    userOutputWriter.writeLine("Unexpected response type.");
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            logger.info("Get command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateGetArguments(arguments);
        return this;
    }

}
