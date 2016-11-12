package weloveclouds.server.store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.utils.FileUtility;

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

    public Set<MovableStorageUnit> copyEntries(HashRange range) {
        Set<MovableStorageUnit> toBeCopied = new HashSet<>();
        for (Path path : filePaths.values()) {
            try {
                toBeCopied.add(
                        new MovableStorageUnit(loadStorageUnitFromPath(path)).copyEntries(range));
            } catch (StorageException e) {
                logger.error(e);
            }
        }
        return toBeCopied;
    }

    public void removeEntries(HashRange range) throws StorageException {
        Set<String> keysToBeRemoved = new HashSet<>();

        for (Path path : filePaths.values()) {
            try {
                MovableStorageUnit storageUnit =
                        new MovableStorageUnit(loadStorageUnitFromPath(path));
                Set<String> removedKeys = storageUnit.removeEntries(range);

                if (!removedKeys.isEmpty()) {
                    if (storageUnit.isEmpty()) {
                        // remove the path if the storage unit is emptied
                        removePathOfAnEmptyStorageUnit(path);
                    } else {
                        // it has free space because some keys were removed
                        if (!unitsWithFreeSpace.contains(path)) {
                            unitsWithFreeSpace.add(path);
                        }
                        // save changes
                        saveStorageUnitToPath(storageUnit, path);
                    }
                    keysToBeRemoved.addAll(removedKeys);
                }
            } catch (StorageException e) {
                logger.error(e);
            } catch (IOException e) {
                logger.error(e);
            }
        }

        for (String key : keysToBeRemoved) {
            removeKeyFromPathEntries(key);
        }
    }

    public void compact() throws StorageException, IOException {
        Iterator<Path> pathIterator = unitsWithFreeSpace.iterator();
        Set<Path> toBeRemovedFromUnitsWithFreeSpace = new HashSet<>();

        try {
            while (pathIterator.hasNext()) {
                Path nextPath = pathIterator.next();
                Path pathAfterNext = pathIterator.next();

                MovableStorageUnit storageUnit =
                        new MovableStorageUnit(loadStorageUnitFromPath(nextPath));
                MovableStorageUnit otherUnit =
                        new MovableStorageUnit(loadStorageUnitFromPath(pathAfterNext));

                Set<String> movedKeys = storageUnit.moveEntries(otherUnit);

                // save the storage units
                saveStorageUnitToPath(storageUnit, nextPath);
                saveStorageUnitToPath(otherUnit, pathAfterNext);

                // update paths for the moved keys
                for (String movedKey : movedKeys) {
                    filePaths.put(movedKey, nextPath);
                }

                // handle if the storage unit to which the data was moved is full
                if (storageUnit.isFull()) {
                    toBeRemovedFromUnitsWithFreeSpace.add(nextPath);
                }

                // handle if the storage unit from which data was moved is empty
                if (otherUnit.isEmpty()) {
                    // remove the file and the path from the store as well
                    FileUtility.deleteFile(pathAfterNext);
                    toBeRemovedFromUnitsWithFreeSpace.add(pathAfterNext);
                }
            }
        } catch (NoSuchElementException ex) {
            // iterator is over
        } finally {
            // remove those paths which are not sufficient for the unitsWithFreeSpace
            for (Path toBeRemoved : toBeRemovedFromUnitsWithFreeSpace) {
                unitsWithFreeSpace.remove(toBeRemoved);
            }
        }
    }

}
