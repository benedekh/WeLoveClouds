package weloveclouds.server.store;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.File;
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
import weloveclouds.server.store.models.PersistedStorageUnit;
import weloveclouds.server.utils.FileUtility;

/**
 * The persistent storage for the {@link DataAccessService}} which stores the key-value pairs on the
 * hard storage.
 * 
 * @author Benedek
 */
public class KVPersistentStorage extends Observable implements IDataAccessService {

    protected static final String FILE_EXTENSION = "ser";

    protected Map<String, PersistedStorageUnit> storageUnits;
    protected Queue<PersistedStorageUnit> unitsWithFreeSpace;

    protected Path rootPath;
    private Logger logger;

    public KVPersistentStorage(Path rootPath) throws IllegalArgumentException {
        if (rootPath == null || !rootPath.toAbsolutePath().toFile().exists()) {
            throw new IllegalArgumentException("Root path does not exist.");
        }

        this.storageUnits = new ConcurrentHashMap<>();
        this.unitsWithFreeSpace = new ArrayDeque<>();

        this.rootPath = rootPath.toAbsolutePath();
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        String key = entry.getKey();
        PutType response;
        PersistedStorageUnit storageUnit = null;

        if (key == null || entry.getValue() == null) {
            throw new StorageException("Key and value cannot be null.");
        } else if (storageUnits.containsKey(key)) {
            response = PutType.UPDATE;
            storageUnit = storageUnits.get(key);
            putEntryIntoStorageUnit(storageUnit, entry);
        } else {
            response = PutType.INSERT;
            // see if there is any storage unit with free spaces
            if (!unitsWithFreeSpace.isEmpty()) {
                // if there is, append the new record to it
                storageUnit = unitsWithFreeSpace.peek();
                putEntryIntoStorageUnit(storageUnit, entry);
                storageUnits.put(key, storageUnit);
            } else {
                String filename = UUID.randomUUID().toString();
                Path path = Paths.get(rootPath.toString(), join(".", filename, FILE_EXTENSION));

                // if there is no, then create a new storage unit
                storageUnit = new PersistedStorageUnit(path);
                storageUnit.putEntry(entry);

                storageUnits.put(key, storageUnit);
                unitsWithFreeSpace.add(storageUnit);
            }
        }

        logger.debug(CustomStringJoiner.join(" ", entry.toString(),
                "is persisted to permanent storage unit."));
        notifyObservers(entry);

        return response;
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        if (!storageUnits.containsKey(key)) {
            throw new ValueNotFoundException(key);
        }

        PersistedStorageUnit storageUnit = storageUnits.get(key);
        String value = storageUnit.getValue(key);

        logger.debug(join("", "Value <", value, "> is read for key <", key, "> ."));
        return value;
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        try {
            if (storageUnits.containsKey(key)) {
                PersistedStorageUnit storageUnit = storageUnits.get(key);
                storageUnit.removeEntry(key);

                if (storageUnit.isEmpty()) {
                    removeStorageUnit(storageUnit);
                } else {
                    if (!storageUnit.isFull() && !unitsWithFreeSpace.contains(storageUnit)) {
                        unitsWithFreeSpace.add(storageUnit);
                    }
                }
            }
        } catch (NullPointerException ex) {
            String errorMessage = "Key cannot be null for removing from persistent storage.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (NoSuchFileException ex) {
            storageUnits.remove(key);
            String errorMessage = CustomStringJoiner.join(" ", "File for key", key,
                    "was already removed from persistent storage.");
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (IOException e) {
            logger.error(e);
            throw new StorageException(
                    "File for key cannot be removed from persistent storage due to permission problems.");
        }

        removeKeyFromStore(key);
    }

    protected void removeKeyFromStore(String key) {
        storageUnits.remove(key);
        notifyObservers(key);
    }

    protected void removeStorageUnit(PersistedStorageUnit storageUnit) throws IOException {
        if (unitsWithFreeSpace.contains(storageUnit)) {
            unitsWithFreeSpace.remove(storageUnit);
        }
        storageUnit.deleteFile();
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
        storageUnits.clear();
        unitsWithFreeSpace.clear();
    }

    /**
     * Scans through the hard storage and notes which keys were already stored in the hard storage
     * on what paths.
     */
    public void loadStorageUnits() {
        logger.debug("Initializing persistent store with already stored keys.");
        clear();

        for (File file : filterFilesInRootPath()) {
            try {
                Path path = file.toPath().toAbsolutePath();
                PersistedStorageUnit storageUnit = loadStorageUnitFromPath(path);
                // load the keys from the storage unit
                for (String key : storageUnit.getKeys()) {
                    storageUnits.put(key, storageUnit);
                    logger.debug(CustomStringJoiner.join(" ", "Key", key,
                            "is put in the persistent store metastore from path", path.toString()));
                }
                if (!storageUnit.isFull()) {
                    unitsWithFreeSpace.add(storageUnit);
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
    protected PersistedStorageUnit loadStorageUnitFromPath(Path path) throws StorageException {
        try {
            return FileUtility.<PersistedStorageUnit>loadFromFile(path);
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
     * Puts the respective entry into a storage unit.
     * 
     * @throws StorageException if any error occurs
     */
    private void putEntryIntoStorageUnit(PersistedStorageUnit storageUnit, KVEntry entry)
            throws StorageException {
        storageUnit.putEntry(entry);

        if (storageUnit.isFull() && unitsWithFreeSpace.contains(storageUnit)) {
            unitsWithFreeSpace.remove(storageUnit);
        }
    }

}
