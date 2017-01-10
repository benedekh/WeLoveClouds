package weloveclouds.commons.cli.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import weloveclouds.commons.cli.models.ParsedUserInput;
import weloveclouds.commons.utils.StringUtils;

/**
 * Abstracts the user input source so different input streams can be used as source.
 *
 * @author Benedek
 */
public class UserInputReader implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(UserInputReader.class);

    private BufferedReader inputReader;
    private AbstractUserInputParser<?> userInputParser;

    /**
     * @param inputStream from which the user input can be read
     */
    public UserInputReader(InputStream inputStream, AbstractUserInputParser<?> userInputParser) {
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
        this.userInputParser = userInputParser;
    }

    /**
     * Reads the user's input through the {@link #inputReader} and parses it into a
     * {@link ParsedUserInput}.
     *
     * @throws IOException see {@link BufferedReader#readLine()}
     */
    public ParsedUserInput<?> readAndParseUserInput() throws IOException {
        String line = inputReader.readLine();
        LOGGER.debug(StringUtils.join(" ", "Line read from the user:", line));
        return userInputParser.parse(line);
    }

    @Override
    public void close() {
        try {
            inputReader.close();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

}
