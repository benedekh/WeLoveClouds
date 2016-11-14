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
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.PersistedStorageUnit;

public class ControllablePersistentStorage extends KVPersistentStorage {

    private volatile boolean writeLockActive;
    private Logger logger;

    public ControllablePersistentStorage(Path rootPath) throws IllegalArgumentException {
        super(rootPath);

        this.writeLockActive = true;
        this.logger = Logger.getLogger(getClass());
    }

    public void setWriteLockActive(boolean isActive) {
        this.writeLockActive = isActive;
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        if (!writeLockActive) {
            return super.putEntry(entry);
        } else {
            throw new StorageException("Persistent storage is not writable.");
        }
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        if (!writeLockActive) {
            super.removeEntry(key);
        } else {
            throw new StorageException("Persistent storage is not modifyable.");
        }
    }

    public void putEntries(Set<MovableStorageUnit> fromStorageUnits) {
        for (MovableStorageUnit storageUnit : fromStorageUnits) {
            try {
                String filename = UUID.randomUUID().toString();
                Path path = Paths.get(rootPath.toString(), join(".", filename, FILE_EXTENSION));
                storageUnit.setPath(path);
                storageUnit.save();

                for (String key : storageUnit.getKeys()) {
                    storageUnits.put(key, storageUnit);
                    notifyObservers(new KVEntry(key, storageUnit.getValue(key)));
                }

                if (!storageUnit.isFull() && !unitsWithFreeSpace.contains(path)) {
                    unitsWithFreeSpace.add(storageUnit);
                }
            } catch (StorageException e) {
                logger.error(e);
            }
        }
    }

    public Set<MovableStorageUnit> copyEntries(HashRange range) {
        Set<MovableStorageUnit> toBeCopied = new HashSet<>();
        for (PersistedStorageUnit storageUnit : storageUnits.values()) {
            toBeCopied.add(new MovableStorageUnit(storageUnit).copyEntries(range));
        }
        return toBeCopied;
    }

    public void removeEntries(HashRange range) throws StorageException {
        Set<String> keysToBeRemoved = new HashSet<>();

        for (PersistedStorageUnit persistedUnit : storageUnits.values()) {
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
            }
        }

        for (String key : keysToBeRemoved) {
            removeKeyFromStore(key);
        }
    }

    public void compact() throws StorageException, IOException {
        Iterator<PersistedStorageUnit> storageUnitIterator = unitsWithFreeSpace.iterator();
        Set<PersistedStorageUnit> toBeRemovedFromUnitsWithFreeSpace = new HashSet<>();

        try {
            while (storageUnitIterator.hasNext()) {
                MovableStorageUnit storageUnit = new MovableStorageUnit(storageUnitIterator.next());
                MovableStorageUnit otherUnit = new MovableStorageUnit(storageUnitIterator.next());
                Set<String> movedKeys = storageUnit.moveEntriesFrom(otherUnit);

                // save the storage units
                storageUnit.save();
                otherUnit.save();

                // update references for the moved keys
                for (String movedKey : movedKeys) {
                    storageUnits.put(movedKey, storageUnit);
                }

                // handle if the storage unit to which the data was moved is full
                if (storageUnit.isFull()) {
                    toBeRemovedFromUnitsWithFreeSpace.add(storageUnit);
                }

                // handle if the storage unit from which data was moved is empty
                if (otherUnit.isEmpty()) {
                    otherUnit.deleteFile();
                    toBeRemovedFromUnitsWithFreeSpace.add(otherUnit);
                }
            }
        } catch (NoSuchElementException ex) {
            // iterator is over
        } finally {
            for (PersistedStorageUnit toBeRemoved : toBeRemovedFromUnitsWithFreeSpace) {
                unitsWithFreeSpace.remove(toBeRemoved);
            }
        }
    }

}
