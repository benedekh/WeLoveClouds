package weloveclouds.evaluation.preparation.transformer;

import static java.nio.file.FileVisitResult.CONTINUE;
import static weloveclouds.evaluation.preparation.util.StringJoinerUtility.join;
import static weloveclouds.evaluation.preparation.util.ValueCleanerUtility.cutValueIntoChunks;
import static weloveclouds.evaluation.preparation.util.ValueCleanerUtility.removeIllegalCharactersFromValue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import weloveclouds.evaluation.preparation.models.PreparedDataset;

public class DatasetTransformer extends SimpleFileVisitor<Path> {

    private static final Logger LOGGER = LogManager.getLogger(DatasetTransformer.class);

    private PreparedDataset dataset;
    private Path rootPath;

    public void transform(final Path path) {
        rootPath = path.toAbsolutePath();
        dataset = new PreparedDataset(rootPath);

        LOGGER.info("Traversing input dataset started.");
        try {
            Files.walkFileTree(rootPath, this);
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            dataset.flushDatasetToCSV();
            LOGGER.info("Traversing input dataset finished.");
        }
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attr) {
        if (attr.isRegularFile()) {
            LOGGER.info(join(": ", "Reading file content started", file.toString()));
            try {
                List<String> fileContent = Files.readAllLines(file);
                String fileContentStr =
                        join("\n", fileContent.toArray(new String[fileContent.size()]));

                Map<String, String> fileContentInKVPairs = cutValueIntoChunks(fileContentStr);
                for (Entry<String, String> kvPair : fileContentInKVPairs.entrySet()) {
                    String key = kvPair.getKey();
                    String value = removeIllegalCharactersFromValue(kvPair.getValue());
                    dataset.putEntry(key, value);
                }
            } catch (IOException ex) {
                LOGGER.error(ex);
            } finally {
                LOGGER.info(join(": ", "Reading file content finished", file.toString()));
            }
        } else {
            LOGGER.warn(join(": ", "It is not a regular file", file.toString()));
        }
        return CONTINUE;
    }

    /**
     * Print each directory visited.
     */
    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) {
        LOGGER.info(join(": ", "Visiting directory finished", dir.toString()));
        return CONTINUE;
    }

    /**
     * If there is some error accessing the file, let the user know. If you don't override this
     * method and an error occurs, an IOException is thrown.
     **/
    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) {
        LOGGER.info(join(": ", "Visiting file failed", file.toString()));
        return CONTINUE;
    }

}
