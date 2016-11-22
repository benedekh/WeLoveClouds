package weloveclouds.evaluation.preparation.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;

import weloveclouds.evaluation.preparation.models.PreparedDataset;

public class DatasetToFileWriter {

    private static final Logger LOGGER = LogManager.getLogger(DatasetToFileWriter.class);

    public static void saveDatasetToPathInCSV(final PreparedDataset dataset,
            final Path outputPath) {
        String outputFilePath = outputPath.toFile().getAbsolutePath();
        char fieldSeparator = ',';

        LOGGER.info("Writing transformed dataset to output CSV.");
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath), fieldSeparator)) {
            Iterator<Entry<String, String>> iterator = dataset.getReadonlyIterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                writer.writeNext(new String[] {entry.getKey(), entry.getValue()});
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info("Writing transformed dataset to output CSV finished.");
        }
    }

}
