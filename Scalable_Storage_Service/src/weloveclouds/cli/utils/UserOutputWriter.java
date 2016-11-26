package weloveclouds.commons.cli.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

/**
 * Abstracts the user output target so different output streams can be used.
 *
 * @author Benedek
 */
public class UserOutputWriter implements AutoCloseable {

    private static final UserOutputWriter instance = new UserOutputWriter();
    private static final Logger LOGGER = Logger.getLogger(UserOutputWriter.class);

    private static String PREFIX = "Client> ";

    private BufferedWriter outputWriter;

    private UserOutputWriter() {
        this.outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));
    }

    /**
     * Set the prefix that is displayed on each row when a message has to be printed on the output
     * stream.
     */
    public static void setPrefix(String prefix) {
        PREFIX = prefix;
    }

    /**
     * Replaces the output stream to which it writes.
     */
    public void setOutputStream(OutputStream stream) {
        close();
        outputWriter = new BufferedWriter(new OutputStreamWriter(stream));
    }

    /**
     * Writes a message to the output stream.
     *
     * @throws IOException see {@link BufferedWriter#write(String)}
     */
    public void writeLine(String message) throws IOException {
        writePrefix();
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    public void appendToLine(String message) throws IOException {
        outputWriter.write(message);
        outputWriter.newLine();
        writePrefix();
        outputWriter.flush();
    }

    /**
     * Writes a single line prefix to the output stream.
     *
     * @throws IOException see {@link BufferedWriter#write(String)}
     */
    public void writePrefix() throws IOException {
        outputWriter.write(PREFIX);
        outputWriter.flush();
    }

    @Override
    public void close() {
        try {
            outputWriter.close();
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    public static UserOutputWriter getInstance() {
        return instance;
    }
}
