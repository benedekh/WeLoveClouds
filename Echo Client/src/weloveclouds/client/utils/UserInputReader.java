package weloveclouds.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import weloveclouds.client.models.ParsedUserInput;

/**
 * 
 * @author Benedek
 */
public class UserInputReader implements AutoCloseable {

    private BufferedReader inputReader;
    private Logger logger;

    public UserInputReader(InputStream inputStream) {
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
        this.logger = Logger.getLogger(getClass());
    }

    public ParsedUserInput readAndParseUserInput() throws IOException {
        String line = inputReader.readLine();
        logger.debug(StringJoiner.join(" ", "Line read from the user:", line, "\n"));
        return UserInputParser.parse(line);
    }

    @Override
    public void close() {
        try {
            inputReader.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

}
