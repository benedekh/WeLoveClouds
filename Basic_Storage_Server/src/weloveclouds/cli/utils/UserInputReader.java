package weloveclouds.cli.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import weloveclouds.client.models.ParsedUserInput;
import weloveclouds.client.utils.CustomStringJoiner;

/**
 * Abstracts the user input source so different input streams can be used as source.
 *
 * @author Benedek
 */
public class UserInputReader implements AutoCloseable {

    private BufferedReader inputReader;
    private Logger logger;

    /**
     * @param inputStream from which the user input can be read
     */
    public UserInputReader(InputStream inputStream) {
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
        this.logger = Logger.getLogger(getClass());
    }

    /**
     * Reads the user's input through the {@link #inputReader} and parses it into a
     * {@link ParsedUserInput}.
     *
     * @throws IOException see {@link BufferedReader#readLine()}
     */
    public ParsedUserInput readAndParseUserInput() throws IOException {
        String line = inputReader.readLine();
        logger.debug(CustomStringJoiner.join(" ", "Line read from the user:", line));
        return UserInputParser.parse(line);
    }

    @Override
    public void close() {
        try {
            inputReader.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

}
