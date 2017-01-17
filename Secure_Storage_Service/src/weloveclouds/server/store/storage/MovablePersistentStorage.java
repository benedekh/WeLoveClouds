package weloveclouds.server.store.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.CloseableLock;
import weloveclouds.commons.utils.PathUtils;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.PersistedStorageUnit;
import weloveclouds.server.store.models.PutType;

/**
 * Represents a {@link KVPersistentStorage}, whose entries and storage units can be filtered,
 * removed, or new storage units can be added to that.
 * 
 * @author Benedek
 */
public class MovablePersistentStorage extends KVPersistentStorage {

    private static final Logger LOGGER = Logger.getLogger(MovablePersistentStorage.class);

    private ReentrantReadWriteLock movingStorageUnitsLock;

    public MovablePersistentStorage(Path rootPath) throws IllegalArgumentException {
        super(rootPath);
        this.movingStorageUnitsLock = new ReentrantReadWriteLock();
    }

    @Override
    public PutType putEntry(KVEntry entry) throws StorageException {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.readLock())) {
            return super.putEntry(entry);
        }
    }

    @Override
    public String getValue(String key) throws StorageException, ValueNotFoundException {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.readLock())) {
            return super.getValue(key);
        }
    }

    @Override
    public void removeEntry(String key) throws StorageException {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.readLock())) {
            super.removeEntry(key);
        }
    }

    /**
     * Saves the entries from the parameter storage units into this persistent storage.
     * 
     * @param fromStorageUnits from where the entries will be copied
     */
    public void putEntries(Set<MovableStorageUnit> fromStorageUnits) throws StorageException {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.writeLock())) {
            LOGGER.debug("Putting storage units from parameter data structure started.");

            for (MovableStorageUnit storageUnit : fromStorageUnits) {
                Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
                storageUnit.setPath(path);
                storageUnit.save();

                for (String key : storageUnit.getKeys()) {
                    storageUnits.put(key, storageUnit);
                    notifyObservers(new KVEntry(key, storageUnit.getValue(key)));
                }

                putStorageUnitIntoFreeSpaceCache(storageUnit);
            }

            LOGGER.debug("Putting storage units from parameter data structure finished.");
        }
    }

    /**
     * Filters those entries from the persistent storage whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @return the storage units of those entries whose keys are in the given range
     * @throws StorageException if an error occurs
     */
    public Set<MovableStorageUnit> filterEntries(HashRange range) {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.readLock())) {
            LOGGER.debug(StringUtils.join(" ",
                    "Filtering storage units according to range filter (", range, ") started."));

            Set<PersistedStorageUnit> storedUnits = new HashSet<>(storageUnits.values());
            Set<MovableStorageUnit> toBeCopied = new HashSet<>();

            for (PersistedStorageUnit storageUnit : storedUnits) {
                toBeCopied.add(new MovableStorageUnit(storageUnit).copyEntries(range));
            }

            LOGGER.debug(StringUtils.join(" ", toBeCopied.size(), " storage units are filtered."));
            return toBeCopied;
        }
    }

    /**
     * Removes those entries from the persistent storage whose keys are in the specified range.
     * 
     * @param range within that shall be the hash values of the keys
     * @throws StorageException if an error occurs
     */
    public void removeEntries(HashRange range) throws StorageException {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.writeLock())) {
            LOGGER.debug(StringUtils.join(" ", "Removing storage units according to range filter (",
                    range, ") started."));

            Set<String> keysToBeRemoved = new HashSet<>();
            Set<PersistedStorageUnit> storedUnits = new HashSet<>(storageUnits.values());
            for (PersistedStorageUnit persistedUnit : storedUnits) {
                try {
                    MovableStorageUnit storageUnit = new MovableStorageUnit(persistedUnit);
                    Set<String> removedKeys = storageUnit.removeEntries(range);

                    if (!removedKeys.isEmpty()) {
                        if (storageUnit.isEmpty()) {
                            removeStorageUnit(storageUnit);
                        } else {
                            putStorageUnitIntoFreeSpaceCache(storageUnit);
                        }

                        keysToBeRemoved.addAll(removedKeys);
                    }
                } catch (IOException e) {
                    LOGGER.error(e);
                    throw new StorageException(
                            "Storage unit cannot be removed due to an IO Error.");
                }
            }

            for (String key : keysToBeRemoved) {
                removeKeyFromStore(key);
            }

            LOGGER.debug(StringUtils.join(" ", String.valueOf(keysToBeRemoved.size()),
                    "storage units were removed from the persistent storage according to the range filter."));
        }
    }

    /**
     * Merges those storage units which are not full yet. Updates the #unitsWithFreeSpace
     * accordingly after the operation is finished.
     */
    public void defragment() {
        try (CloseableLock lock = new CloseableLock(movingStorageUnitsLock.writeLock())) {
            LOGGER.debug("Defragmentation started.");
            Iterator<PersistedStorageUnit> storageUnitIterator =
                    collectNotFullStorageUnits().iterator();
            try {
                PersistedStorageUnit willBeCompacted = storageUnitIterator.next();

                while (storageUnitIterator.hasNext()) {
                    PersistedStorageUnit afterThatWillBeCompacted = storageUnitIterator.next();

                    MovableStorageUnit storageUnit = new MovableStorageUnit(willBeCompacted);
                    MovableStorageUnit otherUnit = new MovableStorageUnit(afterThatWillBeCompacted);
                    Set<String> movedKeys = storageUnit.moveEntriesFrom(otherUnit);

                    try {
                        // save the storage units
                        storageUnit.save();
                        otherUnit.save();
                    } catch (StorageException ex) {
                        LOGGER.error(ex);
                    }

                    // update references for the moved keys
                    for (String movedKey : movedKeys) {
                        storageUnits.put(movedKey, storageUnit);
                    }

                    if (storageUnit.isFull()) {
                        removeStorageUnitFromFreeSpaceCache(storageUnit);
                    } else {
                        // if it is not full, then move data from the forthcoming storage units
                        afterThatWillBeCompacted =
                                defragmentFromCurrent(storageUnit, storageUnitIterator);
                    }

                    if (otherUnit.isEmpty()) {
                        try {
                            removeStorageUnit(otherUnit);
                        } catch (IOException ex) {
                            LOGGER.error(ex);
                        }
                    }
                    willBeCompacted = afterThatWillBeCompacted;
                }
            } catch (NoSuchElementException ex) {
                // iterator is over
                LOGGER.error(ex);
            } finally {
                unitsWithFreeSpace.clear();

                LOGGER.debug("Refreshing the data structure which stores the free stroage units.");
                for (PersistedStorageUnit hasFreeSpace : collectNotFullStorageUnits()) {
                    putStorageUnitIntoFreeSpaceCache(hasFreeSpace);
                }
                LOGGER.debug(StringUtils.join(" ", unitsWithFreeSpace.size(),
                        " storage units have free space after defragmentation."));

            }
            LOGGER.debug("Defragmentation finished.");
        }
    }

    /**
     * Moves data to the current storage unit from those storage units which are still available in
     * the iterator.
     * 
     * @param current to which data will be moved
     * @param iterator over those storage units from which data can be moved
     * 
     * @return a reference to the storage unit from which data was moved the last time
     */
    private PersistedStorageUnit defragmentFromCurrent(MovableStorageUnit current,
            Iterator<PersistedStorageUnit> iterator) {
        try {
            PersistedStorageUnit next = iterator.next();
            MovableStorageUnit nextUnit = new MovableStorageUnit(next);
            Set<String> movedKeys = current.moveEntriesFrom(nextUnit);

            try {
                // save the storage units
                current.save();
                nextUnit.save();
            } catch (StorageException ex) {
                LOGGER.error(ex);
            }

            // update references for the moved keys
            for (String movedKey : movedKeys) {
                storageUnits.put(movedKey, current);
            }

            // handle if the storage unit to which the data was moved is full
            if (current.isFull()) {
                removeStorageUnitFromFreeSpaceCache(current);
            } else {
                next = defragmentFromCurrent(current, iterator);
            }

            // handle if the storage unit from which data was moved is empty
            if (nextUnit.isEmpty()) {
                try {
                    removeStorageUnit(nextUnit);
                } catch (IOException ex) {
                    LOGGER.error(ex);
                }
            }

            return next;
        } catch (NoSuchElementException ex) {
            // iterator is over
            LOGGER.error(ex);
            return null;
        }
    }

    /**
     * @return those storage units which are stored in the {@link #storageUnits} field and have free space.
     */
    private Set<PersistedStorageUnit> collectNotFullStorageUnits() {
        Set<PersistedStorageUnit> result = new HashSet<>();
        for (PersistedStorageUnit storageUnit : new HashSet<>(storageUnits.values())) {
            if (!storageUnit.isFull()) {
                result.add(storageUnit);
            }
        }
        return result;
    }

}
