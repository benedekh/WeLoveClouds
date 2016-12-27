package weloveclouds.server.store;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.services.datastore.DataAccessService;
import weloveclouds.server.services.datastore.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.PersistedStorageUnit;
import weloveclouds.server.store.models.PutType;

/**
 * The persistent storage for the {@link DataAccessService}} which stores the key-value pairs on the
 * hard storage.
 * 
 * @author Benedek
 */
public class KVPersistentStorage extends Observable implements IDataAccessService {

    protected static final String FILE_EXTENSION = "ser";
    private static final Logger LOGGER = Logger.getLogger(KVPersistentStorage.class);

    protected Map<String, PersistedStorageUnit> storageUnits;
    protected Queue<PersistedStorageUnit> unitsWithFreeSpace;

    protected Path rootPath;
    protected ReentrantReadWriteLock loadingFromRootPathLock;

    public KVPersistentStorage(Path rootPath) throws IllegalArgumentException {
        if (rootPath == null || !rootPath.toAbsolutePath().toFile().exists()) {
            throw new IllegalArgumentException("Root path does not exist.");
        }

        this.rootPath = rootPath.toAbsolutePath();
        this.storageUnits = new ConcurrentHashMap<>();
        this.unitsWithFreeSpace = new ConcurrentLinkedDeque<>();
        this.loadingFromRootPathLock = new ReentrantReadWriteLock();
    }

    @Override
    public PutType putEntry(KVEntry entry) throws StorageException {
        try (CloseableLock lock = new CloseableLock(loadingFromRootPathLock.readLock())) {
            String key = entry.getKey();
            PutType response;

            if (key == null || entry.getValue() == null) {
                throw new StorageException("Key and value cannot be null.");
            } else if (storageUnits.containsKey(key)) {
                response = PutType.UPDATE;
                PersistedStorageUnit storageUnit = storageUnits.get(key);
                putEntryIntoStorageUnit(storageUnit, entry);
            } else {
                response = PutType.INSERT;
                // see if there is any storage unit with free spaces
                if (!unitsWithFreeSpace.isEmpty()) {
                    // if there is, append the new record to it
                    PersistedStorageUnit storageUnit = unitsWithFreeSpace.peek();
                    putEntryIntoStorageUnit(storageUnit, entry);
                } else {
                    Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
                    PersistedStorageUnit storageUnit = new PersistedStorageUnit(path);
                    putEntryIntoStorageUnit(storageUnit, entry);
                }
            }

            LOGGER.debug(StringUtils.join(" ", entry, "is persisted to permanent storage unit."));
            notifyObservers(entry);

            return response;
        }
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        try (CloseableLock lock = new CloseableLock(loadingFromRootPathLock.readLock())) {
            if (!storageUnits.containsKey(key)) {
                throw new ValueNotFoundException(key);
            }

            PersistedStorageUnit storageUnit = storageUnits.get(key);
            String value = storageUnit.getValue(key);

            LOGGER.debug(StringUtils.join("", "Value <", value, "> is read for key <", key, "> ."));
            return value;
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try (CloseableLock lock = new CloseableLock(loadingFromRootPathLock.readLock())) {
            try {
                if (storageUnits.containsKey(key)) {
                    PersistedStorageUnit storageUnit = storageUnits.get(key);
                    storageUnit.removeEntry(key);
                    if (storageUnit.isEmpty()) {
                        removeStorageUnit(storageUnit);
                    } else {
                        putStorageUnitIntoFreeSpaceCache(storageUnit);
                    }
                }
            } catch (NullPointerException ex) {
                String errorMessage = "Key cannot be null for removing from persistent storage.";
                LOGGER.error(errorMessage);
                throw new StorageException(errorMessage);
            } catch (NoSuchFileException ex) {
                storageUnits.remove(key);
                String errorMessage = StringUtils.join(" ", "File for key", key,
                        "was already removed from persistent storage.");
                LOGGER.error(errorMessage);
                throw new StorageException(errorMessage);
            } catch (IOException e) {
                LOGGER.error(e);
                throw new StorageException(
                        "File for key cannot be removed from persistent storage due to permission problems.");
            }
            removeKeyFromStore(key);
        }
    }

    @Override
    public void notifyObservers(Object object) {
        setChanged();
        super.notifyObservers(object);
    }

    /**
     * Scans through the hard storage and notes which keys were already stored in the hard storage
     * on what paths.
     */
    public void loadStorageUnitsFromRootPath() {
        try (CloseableLock lock = new CloseableLock(loadingFromRootPathLock.writeLock())) {
            LOGGER.debug("Initializing persistent store with already stored keys.");
            storageUnits.clear();
            unitsWithFreeSpace.clear();

            for (File file : filterFilesInRootPath()) {
                try {
                    Path path = file.toPath().toAbsolutePath();
                    PersistedStorageUnit storageUnit = loadStorageUnitFromPath(path);
                    // load the keys from the storage unit
                    for (String key : storageUnit.getKeys()) {
                        storageUnits.put(key, storageUnit);
                        LOGGER.debug(StringUtils.join(" ", "Key", key,
                                "is put in the persistent store metastore from path", path));
                    }
                    putStorageUnitIntoFreeSpaceCache(storageUnit);
                } catch (StorageException ex) {
                    LOGGER.error(StringUtils.join(" ", file, ex));
                }
            }
            LOGGER.debug("Initializing persistent store finished.");
        }
    }

    /**
     * Puts the respective storage unit into the cache for free storage units, if the storage unit
     * is not full and the cache does not contain it yet.
     */
    protected void putStorageUnitIntoFreeSpaceCache(PersistedStorageUnit storageUnit) {
        if (!storageUnit.isFull() && !unitsWithFreeSpace.contains(storageUnit)) {
            unitsWithFreeSpace.add(storageUnit);
        }
    }

    /**
     * Removes the respective storage unit from the cache for free storage units.
     */
    protected void removeStorageUnitFromFreeSpaceCache(PersistedStorageUnit storageUnit) {
        unitsWithFreeSpace.remove(storageUnit);
    }

    /**
     * Removes the respective key from the store.
     */
    protected void removeKeyFromStore(String key) {
        storageUnits.remove(key);
        notifyObservers(key);
    }

    /**
     * Removes the respective storage unit from the cache for free storage units.
     */
    protected void removeStorageUnit(PersistedStorageUnit storageUnit) throws IOException {
        removeStorageUnitFromFreeSpaceCache(storageUnit);
        storageUnit.deleteFile();
    }

    /**
     * Loads and returns the storage unit stored in the file denoted by its path.
     * 
     * @throws StorageException if any error occurs
     */
    private PersistedStorageUnit loadStorageUnitFromPath(Path path) throws StorageException {
        try {
            return PathUtils.<PersistedStorageUnit>loadFromFile(path);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new StorageException(
                    "Storage unit was not read from persistent storage due to IO error.");
        } catch (ClassNotFoundException | ClassCastException ex) {
            LOGGER.error(ex);
            throw new StorageException(
                    "Storage unit was not read from persistent storage due to format conversion error.");
        }
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
     * Puts the respective entry into a storage unit. In case the storage unit is full, it creates a
     * new {@link PersistedStorageUnit} and puts the entry into that.<br>
     * Besides, it puts the parameter respective storageUnit into the {@link #storageUnits} map in
     * case the entry was put into that. Updates the #unitsWithFreeSpace accordingly too.
     * 
     * @throws StorageException if any error occurs
     */
    private void putEntryIntoStorageUnit(PersistedStorageUnit storageUnit, KVEntry entry)
            throws StorageException {
        String key = entry.getKey();
        try {
            storageUnit.putEntry(entry);
            if (!storageUnits.containsKey(key)) {
                synchronized (this) {
                    if (!storageUnits.containsKey(key)) {
                        // if it is the first time we put the key
                        storageUnits.put(key, storageUnit);
                        putStorageUnitIntoFreeSpaceCache(storageUnit);
                    } else {
                        // if the key was put concurrently to the storage
                        // just beforehand us, then update the value in that
                        // storage unit, and delete the one that we created
                        PersistedStorageUnit storedStorageUnit = storageUnits.get(key);
                        storedStorageUnit.putEntry(entry);
                        storageUnits.put(key, storedStorageUnit);
                        try {
                            storageUnit.deleteFile();
                        } catch (IOException ex) {
                            LOGGER.error(ex);
                        }
                    }
                }
            } else {
                // if the key was already stored
                storageUnits.put(key, storageUnit);
                putStorageUnitIntoFreeSpaceCache(storageUnit);
            }
        } catch (UnsupportedOperationException ex) {
            removeStorageUnitFromFreeSpaceCache(storageUnit);

            Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
            PersistedStorageUnit newStorageUnit = new PersistedStorageUnit(path);

            newStorageUnit.putEntry(entry);
            storageUnits.put(key, newStorageUnit);
            putStorageUnitIntoFreeSpaceCache(newStorageUnit);
        }
    }

}
