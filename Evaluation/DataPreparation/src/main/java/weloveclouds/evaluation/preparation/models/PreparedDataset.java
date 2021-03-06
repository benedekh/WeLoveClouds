package weloveclouds.evaluation.preparation.models;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import weloveclouds.evaluation.preparation.util.DatasetToFileWriter;
import weloveclouds.evaluation.preparation.util.KeyCreatorUtility;

/**
 * The type Prepared dataset.
 * 
 * @author Benedek
 */
public class PreparedDataset {

    private static int MAX_NUMBER_OF_ENTRIES = 10 * 1000;

    private Map<String, String> dataset;
    private Path rootPath;
    private int numberOfFlushes;

    /**
     * Instantiates a new Prepared dataset.
     *
     * @param rootPath the root path
     */
    public PreparedDataset(final Path rootPath) {
        this.dataset = new HashMap<>();
        this.rootPath = rootPath.toAbsolutePath().getParent();
        this.numberOfFlushes = 0;
    }

    /**
     * Put entry.
     *
     * @param key the key
     * @param value the value
     */
    public void putEntry(final String key, final String value) {
        if (dataset.size() == MAX_NUMBER_OF_ENTRIES) {
            flushDatasetToCSV();
            numberOfFlushes++;
        }

        String newKey = new String(key);
        while (dataset.containsKey(newKey)) {
            newKey = KeyCreatorUtility.generate20BytesKey();
        }
        dataset.put(newKey, value);
    }

    /**
     * Gets readonly iterator.
     *
     * @return the readonly iterator
     */
    public Iterator<Entry<String, String>> getReadonlyIterator() {
        return Collections.unmodifiableMap(dataset).entrySet().iterator();
    }

    /**
     * Flush dataset to csv.
     */
    public void flushDatasetToCSV() {
        Path csvPath = Paths.get(new File(rootPath.toFile().getAbsolutePath(),
                String.valueOf(numberOfFlushes) + ".csv").getAbsolutePath());
        DatasetToFileWriter.saveDatasetToPathInCSV(this, csvPath);
        dataset.clear();
    }

}
