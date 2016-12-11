package weloveclouds.client.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.client.utils.PutCommandUtils;
import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.commons.kvstore.models.IKVMessage;

/**
 * Put command which means the client would like to send a key along with a value to the server.
 *
 * @author Benoit, Benedek, Hunton
 */
public class Put extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private Logger logger;

    /**
     * @param arguments contains the key in the {@link #KEY_INDEX} position and the value is merged
     *        into one value starting from the index {@link #VALUE_INDEX} and going until the end of
     *        the array
     * @param communicationApi which is used for querying the value from the server
     */
    public Put(String[] arguments, IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing put command.");
            String key = arguments[KEY_INDEX];
            String value = PutCommandUtils.mergeValuesToOneString(VALUE_INDEX, arguments);

            IKVMessage response = communicationApi.put(key, value);
            logger.debug(response.toString());

            switch (response.getStatus()) {
                case PUT_UPDATE:
                    userOutputWriter.writeLine("Key was successfully updated on the server.");
                    break;
                case PUT_SUCCESS:
                    userOutputWriter.writeLine("Key was successfully put on the server.");
                    break;
                case PUT_ERROR:
                    userOutputWriter.writeLine(CustomStringJoiner.join(" ", "Error during key put:",
                            response.getValue()));
                    break;
                case DELETE_SUCCESS:
                    userOutputWriter.writeLine("Key removed successfully.");
                    break;
                case DELETE_ERROR:
                    userOutputWriter
                            .writeLine(join(" ", "Error during key remove:", response.getValue()));
                    break;
                default:
                    userOutputWriter.writeLine("Unexpected response type.");
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
            throw new ClientSideException(e.getMessage(), e);
        } finally {
            logger.info("Put command execution finished.");
        }
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validatePutArguments(arguments);
        return this;
    }


}
