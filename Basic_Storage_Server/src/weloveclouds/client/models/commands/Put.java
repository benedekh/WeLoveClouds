package weloveclouds.client.models.commands;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.ArgumentsValidator;
import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.communication.exceptions.ClientSideException;
import weloveclouds.kvstore.models.IKVMessage;

public class Put extends AbstractKVCommunicationApiCommand {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private Logger logger;

    public Put(String[] arguments, IKVCommunicationApi communicationApi) {
        super(arguments, communicationApi);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void execute() throws ClientSideException {
        try {
            logger.info("Executing put command.");
            String key = arguments[KEY_INDEX];
            String value = mergeValuesToOneString(arguments);

            IKVMessage response = communicationApi.put(key, value);
            logger.debug(response.toString());

            switch (response.getStatus()) {
                case PUT_UPDATE:
                case PUT_SUCCESS:
                case PUT_ERROR:
                    userOutputWriter.writeLine(response.getValue());
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

    private String mergeValuesToOneString(String[] arguments) {
        List<String> argList = Arrays.asList(arguments);
        List<String> valueElements = argList.subList(VALUE_INDEX, argList.size() - 1);
        return join(" ", valueElements);
    }

}
