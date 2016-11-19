package weloveclouds.server.store;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;
import weloveclouds.server.store.models.PersistedStorageUnit;

/**
 * Represents a {@link KVPersistentStorage}, whose entries and storage units can be filtered,
 * removed, or new storage units can be added to that.
 * 
 * Besides, the {@link #putEntry(KVEntry)} and {@link #removeEntry(String)} method's accessibility
 * can be limited by applying a {@link #writeLockActive} flag.
 * 
 * @author Benedek
 */
public class MovablePersistentStorage extends KVPersistentStorage
        implements IMovableDataAccessService {

    private Logger logger;

    public MovablePersistentStorage(Path rootPath) throws IllegalArgumentException {
        super(rootPath);
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    public void putEntries(MovableStorageUnits fromStorageUnits) throws StorageException {
        for (MovableStorageUnit storageUnit : fromStorageUnits.getStorageUnits()) {
            String filename = UUID.randomUUID().toString();
            Path path = Paths.get(rootPath.toString(), join(".", filename, FILE_EXTENSION));
            storageUnit.setPath(path);
            storageUnit.save();

            for (String key : storageUnit.getKeys()) {
                storageUnits.put(key, storageUnit);
                notifyObservers(new KVEntry(key, storageUnit.getValue(key)));
            }

            if (!storageUnit.isFull() && !unitsWithFreeSpace.contains(storageUnit)) {
                unitsWithFreeSpace.add(storageUnit);
            }
        }
    }

    @Override
    public MovableStorageUnits filterEntries(HashRange range) {
        Set<PersistedStorageUnit> storedUnits = new HashSet<>(storageUnits.values());
        Set<MovableStorageUnit> toBeCopied = new HashSet<>();

        for (PersistedStorageUnit storageUnit : storedUnits) {
            toBeCopied.add(new MovableStorageUnit(storageUnit).copyEntries(range));
        }

        return new MovableStorageUnits(toBeCopied);
    }

    @Override
    public void removeEntries(HashRange range) throws StorageException {
        Set<PersistedStorageUnit> storedUnits = new HashSet<>(storageUnits.values());
        Set<String> keysToBeRemoved = new HashSet<>();

        for (PersistedStorageUnit persistedUnit : storedUnits) {
            try {
                MovableStorageUnit storageUnit = new MovableStorageUnit(persistedUnit);
                Set<String> removedKeys = storageUnit.removeEntries(range);

                if (!removedKeys.isEmpty()) {
                    if (storageUnit.isEmpty()) {
                        removeStorageUnit(storageUnit);
                    } else if (!unitsWithFreeSpace.contains(storageUnit)) {
                        // it has free space because some keys were removed
                        unitsWithFreeSpace.add(storageUnit);
                    }

                    keysToBeRemoved.addAll(removedKeys);
                }
            } catch (IOException e) {
                logger.error(e);
                throw new StorageException("Storage unit cannot be removed due to an IO Error.");
            }
        }

        for (String key : keysToBeRemoved) {
            removeKeyFromStore(key);
        }
    }

    /**
     * Merges those storage units which are not full yet. Updates the #unitsWithFreeSpace
     * accordingly after the operation is finished.
     */
    @Override
    public void defragment() {
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
                    logger.error(ex);
                }

                // update references for the moved keys
                for (String movedKey : movedKeys) {
                    storageUnits.put(movedKey, storageUnit);
                }

                if (storageUnit.isFull()) {
                    // handle if the storage unit to which the data was moved is full
                    unitsWithFreeSpace.remove(storageUnit);
                } else {
                    // if it is not full, then move data from the forthcoming storage units
                    afterThatWillBeCompacted =
                            defragmentFromCurrent(storageUnit, storageUnitIterator);
                }

                if (otherUnit.isEmpty()) {
                    // handle if the storage unit from which data was moved is empty
                    try {
                        otherUnit.deleteFile();
                    } catch (IOException ex) {
                        logger.error(ex);
                    }
                    unitsWithFreeSpace.remove(otherUnit);
                }

                willBeCompacted = afterThatWillBeCompacted;
            }
        } catch (NoSuchElementException ex) {
            // iterator is over
        } finally {
            unitsWithFreeSpace.clear();

            for (PersistedStorageUnit hasFreeSpace : collectNotFullStorageUnits()) {
                if (!unitsWithFreeSpace.contains(hasFreeSpace)) {
                    unitsWithFreeSpace.add(hasFreeSpace);
                }
            }
        }
    }

    /**
     * Moves data to the current storage unit from those storage units which are still availabile in
     * the iterator.
     * 
     * @param current to which data will be moved
     * @param iterator over those storage units from which data can be moved
     * 
     * @return a reference to the storage unit from which data was moved the last time
     */
    private PersistedStorageUnit defragmentFromCurrent(MovableStorageUnit current,
            Iterator<PersistedStorageUnit> iterator) {

        PersistedStorageUnit next = iterator.next();
        MovableStorageUnit nextUnit = new MovableStorageUnit(next);
        Set<String> movedKeys = current.moveEntriesFrom(nextUnit);

        try {
            // save the storage units
            current.save();
            nextUnit.save();
        } catch (StorageException ex) {
            logger.error(ex);
        }

        // update references for the moved keys
        for (String movedKey : movedKeys) {
            storageUnits.put(movedKey, current);
        }

        // handle if the storage unit to which the data was moved is full
        if (current.isFull()) {
            unitsWithFreeSpace.remove(current);
        } else {
            next = defragmentFromCurrent(current, iterator);
        }

        // handle if the storage unit from which data was moved is empty
        if (nextUnit.isEmpty()) {
            try {
                nextUnit.deleteFile();
            } catch (IOException ex) {
                logger.error(ex);
            }
            unitsWithFreeSpace.remove(nextUnit);
        }

        return next;
    }

    /**
     * @return those storage units which are stored in the #storageUnits field and have free space.
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
