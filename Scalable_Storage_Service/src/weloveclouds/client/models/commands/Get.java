package weloveclouds.client.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.api.IKVCommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.kvstore.models.messages.IKVMessage;

/**
 * Get command which means the client would like to query the value for a respective key.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Get extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(Get.class);

    /**
     * @param arguments contains the key in the {@link #KEY_INDEX} position
     * @param communicationApi which is used for querying the value from the server
     */
    public Get(String[] arguments, IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            LOGGER.info("Executing get command.");

            IKVMessage response = communicationApi.get(arguments[KEY_INDEX]);
            LOGGER.debug(response.toString());
            String responseValue = response.getValue();

            switch (response.getStatus()) {
                case GET_SUCCESS:
                    userOutputWriter.writeLine(CustomStringJoiner.join(" ", "Value", responseValue,
                            "was sucessfully got for key."));
                    break;
                case GET_ERROR:
                    userOutputWriter.writeLine(join(" ", "Error during key get:", responseValue));
                    break;
                default:
                    userOutputWriter.writeLine("Unexpected response type.");
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            LOGGER.info("Get command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateGetArguments(arguments);
        return this;
    }

}
