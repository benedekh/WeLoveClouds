package weloveclouds.evalution.dataloading.csvtraverser;

import static weloveclouds.evaluation.dataloading.util.StringJoinerUtility.join;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;

import weloveclouds.evaluation.dataloading.util.FieldSizeValidator;
import weloveclouds.evaluation.dataloading.util.StringJoinerUtility;

public class CSVTraverser {

    private static final Logger LOGGER = LogManager.getLogger(CSVTraverser.class);

    public void traverseFolder(Path csvFolderPath) {
        File[] filesToBeVisited = csvFolderPath.toAbsolutePath().toFile().listFiles();
        try (Stream<File> files = Arrays.asList(filesToBeVisited).stream()) {
            files.filter(file -> file.getName().endsWith(".csv"))
                    .forEach(file -> readCSVFile(file));
        }
    }

    private void readCSVFile(File file) {
        char fieldSeparator = ',';
        char fieldQuote = '\"';

        LOGGER.info(join(": ", "Reading file content started", file.toString()));
        try (CSVReader reader = new CSVReader(new FileReader(file), fieldSeparator, fieldQuote)) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String key = nextLine[0];
                String value = nextLine[1];

                if (FieldSizeValidator.isKeySizeOverLimit(key)) {
                    LOGGER.error(StringJoinerUtility.join(": ", "Key is oversized by " + key.getBytes(StandardCharsets.US_ASCII).length, key));
                }
                if (FieldSizeValidator.isValueSizeOverLimit(value)) {
                    LOGGER.error(StringJoinerUtility.join(": ", "Value is oversized", value));
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info(join(": ", "Reading file content finished", file.toString()));
        }
    }

}
