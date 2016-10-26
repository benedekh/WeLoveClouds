package weloveclouds.client.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

/**
 * 
 * @author Benedek
 */
public class UserOutputWriter implements AutoCloseable {
    private static final UserOutputWriter instance = new UserOutputWriter();
    private static final String PREFIX = "EchoClient> ";

    private BufferedWriter outputWriter;
    private Logger logger;


    private UserOutputWriter() {
        this.outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        this.logger = Logger.getLogger(getClass());
    }

    public void setOutputStream(OutputStream stream) {
        close();
        outputWriter = new BufferedWriter(new OutputStreamWriter(stream));
    }

    public void writeLine(String message) throws IOException {
        writePrefix();
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    public void writePrefix() throws IOException {
        outputWriter.write(PREFIX);
        outputWriter.flush();
    }

    @Override
    public void close() {
        try {
            outputWriter.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static UserOutputWriter getInstance() {
        return instance;
    }
}
