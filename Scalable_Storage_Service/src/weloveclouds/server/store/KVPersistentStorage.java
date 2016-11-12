package weloveclouds.server.store;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.PersistentStorageUnit;
import weloveclouds.server.utils.FileUtility;

/**
 * The persistent storage for the {@link DataAccessService}} which stores the key-value pairs on the
 * hard storage.
 * 
 * @author Benedek
 */
public class KVPersistentStorage extends Observable implements IDataAccessService {

    protected static final String FILE_EXTENSION = "ser";
    protected static final int MAX_NUMBER_OF_ENTRIES = 100;

    protected Map<String, Path> filePaths;
    protected Queue<Path> unitsWithFreeSpace;

    protected Path rootPath;
    private Logger logger;

    public KVPersistentStorage(Path rootPath) throws IllegalArgumentException {
        if (rootPath == null || !rootPath.toAbsolutePath().toFile().exists()) {
            throw new IllegalArgumentException("Root path does not exist.");
        }

        this.filePaths = new ConcurrentHashMap<>();
        this.unitsWithFreeSpace = new ArrayDeque<>();

        this.rootPath = rootPath.toAbsolutePath();
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        String key = entry.getKey();
        Path path = null;
        PutType response;

        if (key == null || entry.getValue() == null) {
            throw new StorageException("Key and value cannot be null.");
        } else if (filePaths.containsKey(key)) {
            response = PutType.UPDATE;
            Path filePath = filePaths.get(key);
            putEntryIntoPersistedStorageUnit(entry, filePath);
        } else {
            response = PutType.INSERT;

            // see if there is any storage unit with free spaces
            if (!unitsWithFreeSpace.isEmpty()) {
                // if there is, append the new record to it
                path = unitsWithFreeSpace.peek();
                putEntryIntoPersistedStorageUnit(entry, path);
                filePaths.put(key, path);
            } else {
                // if there is no, then create a new storage unit
                PersistentStorageUnit storageUnit =
                        new PersistentStorageUnit(MAX_NUMBER_OF_ENTRIES);
                storageUnit.putEntry(entry);

                String filename = UUID.randomUUID().toString();
                path = Paths.get(rootPath.toString(), join(".", filename, FILE_EXTENSION));
                saveStorageUnitToPath(storageUnit, path);

                filePaths.put(key, path);
                unitsWithFreeSpace.add(path);
            }
        }

        logger.debug(CustomStringJoiner.join(" ", entry.toString(),
                "is persisted to permanent store on path ", path.toString()));
        notifyObservers(entry);

        return response;
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        if (!filePaths.containsKey(key)) {
            throw new ValueNotFoundException(key);
        }

        Path path = filePaths.get(key);
        PersistentStorageUnit storageUnit = loadStorageUnitFromPath(path);
        String value = storageUnit.getValue(key);

        logger.debug(join("", "Value <", value, "> is read for key <", key, "> from file ",
                path.toString()));
        return value;
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        try {
            if (filePaths.containsKey(key)) {
                Path path = filePaths.get(key);
                PersistentStorageUnit storageUnit = loadStorageUnitFromPath(path);
                storageUnit.removeEntry(key);

                if (storageUnit.isEmpty()) {
                    removePathOfAnEmptyStorageUnit(path);
                } else {
                    saveStorageUnitToPath(storageUnit, path);
                    if (!storageUnit.isFull() && !unitsWithFreeSpace.contains(path)) {
                        unitsWithFreeSpace.add(path);
                    }
                }
            }
        } catch (NullPointerException ex) {
            String errorMessage = "Key cannot be null for removing from persistent storage.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (NoSuchFileException ex) {
            filePaths.remove(key);
            String errorMessage = CustomStringJoiner.join(" ", "File for key", key,
                    "was already removed from persistent storage.");
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (IOException e) {
            logger.error(e);
            throw new StorageException(
                    "File for key cannot be removed from persistent storage due to permission problems.");
        }

        removeKeyFromPathEntries(key);
    }

    protected void removeKeyFromPathEntries(String key) {
        filePaths.remove(key);
        notifyObservers(key);
    }

    protected void removePathOfAnEmptyStorageUnit(Path path) throws IOException {
        if (unitsWithFreeSpace.contains(path)) {
            unitsWithFreeSpace.remove(path);
        }
        FileUtility.deleteFile(path);
    }

    @Override
    public void notifyObservers(Object object) {
        setChanged();
        super.notifyObservers(object);
    }

    /**
     * Clears the internal meta-data cache structures of the persistent storage.
     */
    public void clear() {
        filePaths.clear();
        unitsWithFreeSpace.clear();
    }

    /**
     * Scans through the hard storage and notes which keys were already stored in the hard storage
     * on what paths.
     */
    public void initializePaths() {
        logger.debug("Initializing persistent store with already stored keys.");
        clear();

        for (File file : filterFilesInRootPath()) {
            try {
                Path path = file.toPath().toAbsolutePath();
                PersistentStorageUnit storageUnit = loadStorageUnitFromPath(path);
                // load the keys from the storage unit
                for (String key : storageUnit.getKeys()) {
                    filePaths.put(key, path);
                    logger.debug(CustomStringJoiner.join(" ", "Key", key,
                            "is put in the persistent store metastore from path", path.toString()));
                }
                if (!storageUnit.isFull()) {
                    unitsWithFreeSpace.add(path);
                }
            } catch (StorageException ex) {
                logger.error(join(" ", file.toString(), ex.getMessage()));
            }
        }
        logger.debug("Initializing persistent store finished.");
    }

    /**
     * Filters the file names inside the {@link #rootPath} if they end with {@link #FILE_EXTENSION}.
     */
    private File[] filterFilesInRootPath() {
        return rootPath.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(FILE_EXTENSION);
            }
        });
    }

    /**
     * Loads and returns the storage unit stored in the file denoted by its path.
     * 
     * @throws StorageException if any error occurs
     */
    protected PersistentStorageUnit loadStorageUnitFromPath(Path path) throws StorageException {
        try {
            return FileUtility.<PersistentStorageUnit>loadFromFile(path);
        } catch (IOException ex) {
            logger.error(ex);
            throw new StorageException(
                    "Storage unit was not read from persistent storage due to IO error.");
        } catch (ClassNotFoundException | ClassCastException ex) {
            logger.error(ex);
            throw new StorageException(
                    "Storage unit was not read from persistent storage due to format conversion error.");
        }
    }

    /**
     * Saves the respective storage unit into a file denoted by its path.
     * 
     * @throws StorageException if any error occurs
     */
    protected void saveStorageUnitToPath(PersistentStorageUnit storageUnit, Path path)
            throws StorageException {
        try {
            FileUtility.<PersistentStorageUnit>saveToFile(path, storageUnit);
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new StorageException("File was not found.");
        } catch (IOException e) {
            logger.error(e);
            throw new StorageException(
                    "Storage unit was not saved to the persistent storage due to IO error.");
        }
    }

    /**
     * Puts the respective entry into a storage unit which is stored in the file denoted by its
     * path. It automatically loads the storage unit, puts the entry into it, and saves to storage
     * unit to the same file as it was stored before.
     * 
     * @throws StorageException if any error occurs
     */
    private void putEntryIntoPersistedStorageUnit(KVEntry entry, Path path)
            throws StorageException {
        PersistentStorageUnit storageUnit = loadStorageUnitFromPath(path);
        storageUnit.putEntry(entry);
        saveStorageUnitToPath(storageUnit, path);

        if (storageUnit.isFull() && unitsWithFreeSpace.contains(path)) {
            unitsWithFreeSpace.remove(path);
        }
    }

}
