package weloveclouds.evaluation.dataloading.csvtraverser;

import static weloveclouds.commons.utils.StringUtils.join;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.evaluation.dataloading.connection.ClientConnection;
import weloveclouds.evaluation.dataloading.util.Operation;

/**
 * The type Csv folder traverser.
 * 
 * @author Benedek
 */
public class CSVFolderTraverser {
    private static final Logger LOGGER = LogManager.getLogger(CSVFolderTraverser.class);

    private static final String FIELD_SEPARATOR = "-Ł-";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private ClientConnection client;

    /**
     * Instantiates a new Csv folder traverser.
     *
     * @param client the client
     */
    public CSVFolderTraverser(ClientConnection client) {
        this.client = client;
        this.client.connect();
    }

    /**
     * Traverse folder.
     *
     * @param csvFolderPath the csv folder path
     * @param operation type of operation to perform on each key-value pair
     */
    public void traverseFolder(Path csvFolderPath, Operation operation) {
        File[] filesToBeVisited = csvFolderPath.toAbsolutePath().toFile().listFiles();
        LOGGER.info("Traversing input csv folder started.");
        try (Stream<File> files = Arrays.asList(filesToBeVisited).stream()) {
            files.filter(file -> file.getName().endsWith(".csv"))
                    .forEach(file -> readCSVFile(file, operation));
        }
        LOGGER.info("Traversing input csv folder finished.");
    }

    private void readCSVFile(File file, Operation operation) {
        LOGGER.info(join(": ", "Reading file content started", file.toString()));
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(FIELD_SEPARATOR);
                String key = parts[KEY_INDEX];
                String value = null;

                switch (operation) {
                    case GET:
                        client.get(key, false);
                        break;
                    case PUT:
                        value = parts[VALUE_INDEX];
                    case DELETE:
                        client.put(key, value, false);
                        break;
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info(join(": ", "Reading file content finished"));
        }
    }

}
