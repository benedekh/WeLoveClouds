package weloveclouds.evalution.dataloading.csvtraverser;

import static weloveclouds.evaluation.dataloading.util.StringJoinerUtility.join;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.evaluation.dataloading.util.FieldSizeValidator;
import weloveclouds.evaluation.dataloading.util.StringJoinerUtility;

public class CSVFolderTraverser {

    private static final String FIELD_SEPARATOR = "-Ł-";
    private static final Logger LOGGER = LogManager.getLogger(CSVFolderTraverser.class);
    
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public void traverseFolder(Path csvFolderPath) {
        File[] filesToBeVisited = csvFolderPath.toAbsolutePath().toFile().listFiles();
        LOGGER.info("Traversing input csv folder started.");
        try (Stream<File> files = Arrays.asList(filesToBeVisited).stream()) {
            files.filter(file -> file.getName().endsWith(".csv"))
                    .forEach(file -> readCSVFile(file));
        }
        LOGGER.info("Traversing input csv folder finished.");
    }

    private void readCSVFile(File file) {
        LOGGER.info(join(": ", "Reading file content started", file.toString()));
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                i++;
                String[] parts = line.split(FIELD_SEPARATOR);
                String key = parts[KEY_INDEX];
                String value = parts[VALUE_INDEX];

                if (FieldSizeValidator.isKeySizeOverLimit(key)) {
                    LOGGER.error(StringJoinerUtility.join(": ",
                            "Key is oversized by " + key.getBytes(StandardCharsets.US_ASCII).length,
                            key));
                }
                if (FieldSizeValidator.isValueSizeOverLimit(value)) {
                    LOGGER.error(
                            StringJoinerUtility.join(": ",
                                    "Value is oversized by "
                                            + value.getBytes(StandardCharsets.US_ASCII).length,
                                    value));
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info(join(": ", "Reading file content finished", file.toString()));
            LOGGER.info(join(": ", "Number of lines read", String.valueOf(i)));
        }
    }

}
