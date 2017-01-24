package weloveclouds.evaluation.preparation.util;

import static weloveclouds.evaluation.preparation.util.StringUtils.join;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.evaluation.preparation.models.PreparedDataset;

/**
 * The type Dataset to file writer.
 * 
 * @author Benedek
 */
public class DatasetToFileWriter {

    private static final String FIELD_SEPARATOR = "-Ł-";
    private static final Logger LOGGER = LogManager.getLogger(DatasetToFileWriter.class);

    /**
     * Save dataset to path in csv.
     *
     * @param dataset the dataset
     * @param outputPath the output path
     */
    public static void saveDatasetToPathInCSV(final PreparedDataset dataset,
            final Path outputPath) {
        String outputFilePath = outputPath.toFile().getAbsolutePath();

        LOGGER.info("Writing transformed dataset to output CSV.");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            Iterator<Entry<String, String>> iterator = dataset.getReadonlyIterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String line = join(FIELD_SEPARATOR, entry.getKey(), entry.getValue());

                writer.write(line);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            LOGGER.info("Writing transformed dataset to output CSV finished.");
        }
    }

}
