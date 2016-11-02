package weloveclouds.server.store;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.server.services.IDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

public class KVPersistentStorage extends Observable implements IDataAccessService {

    private static final String FILE_EXTENSION = "ser";

    private Map<String, Path> persistentPaths;
    private Path rootPath;

    private Logger logger;

    public KVPersistentStorage(Path rootPath) throws IllegalArgumentException {
        if (rootPath == null || !rootPath.toAbsolutePath().toFile().exists()) {
            throw new IllegalArgumentException("Root path does not exist.");
        }

        this.persistentPaths = new HashMap<>();
        this.rootPath = rootPath.toAbsolutePath();
        this.logger = Logger.getLogger(getClass());

        initializePaths();
    }

    @Override
    public synchronized PutType putEntry(KVEntry entry) throws StorageException {
        String key = entry.getKey();
        PutType response;

        if (key == null || entry.getValue() == null) {
            throw new StorageException("Key and value cannot be null.");
        } else if (persistentPaths.containsKey(key)) {
            response = PutType.UPDATE;
            removeEntryWithoutNotification(key);
        } else {
            response = PutType.INSERT;
        }

        String cleanKey = key.replaceAll("[^a-zA-Z0-9.-]", "_"); // valid filename
        Path entryPath = Paths.get(rootPath.toString(), join(".", cleanKey, FILE_EXTENSION));

        try (ObjectOutputStream stream =
                new ObjectOutputStream(new FileOutputStream(entryPath.toString()))) {
            stream.writeObject(entry);
            logger.debug(CustomStringJoiner.join(" ", entry.toString(),
                    "is persisted to permanent store on path ", entryPath.toString()));

            persistentPaths.put(key, entryPath);
            notifyObservers(entry);

            return response;
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new StorageException("File was not found.");
        } catch (IOException e) {
            logger.error(e);
            throw new StorageException(
                    "Entry was not saved to the persistent storage due to IO error.");
        }
    }

    @Override
    public synchronized String getValue(String key)
            throws StorageException, ValueNotFoundException {
        Path path = persistentPaths.get(key);
        if (path == null) {
            throw new ValueNotFoundException(key);
        }
        KVEntry entry = readEntryFromFile(path.toFile());
        logger.debug(CustomStringJoiner.join(" ", entry.toString(), "is read from file",
                path.toString()));
        return entry.getValue();
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        removeEntryWithoutNotification(key);
        notifyObservers(key);
    }

    @Override
    public void notifyObservers(Object object) {
        setChanged();
        super.notifyObservers(object);
    }

    private void removeEntryWithoutNotification(String key) throws StorageException {
        try {
            if (persistentPaths.containsKey(key)) {
                Path path = persistentPaths.get(key);
                Files.delete(path);
                persistentPaths.remove(key);
                logger.debug(CustomStringJoiner.join(" ", key,
                        "is removed from persistent store, along with file", path.toString()));
            }
        } catch (NullPointerException ex) {
            String errorMessage = "Key cannot be null for removing from persistent storage.";
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (NoSuchFileException ex) {
            persistentPaths.remove(key);
            String errorMessage = CustomStringJoiner.join(" ", "File for key", key,
                    "was already removed from persistent storage.");
            logger.error(errorMessage);
            throw new StorageException(errorMessage);
        } catch (IOException e) {
            logger.error(e);
            throw new StorageException(
                    "File for key cannot be removed from persistent storage due to permission problems.");
        }
    }

    private void initializePaths() {
        for (File file : filterFilesInRootPath()) {
            try {
                KVEntry entry = readEntryFromFile(file);
                persistentPaths.put(entry.getKey(), file.toPath().toAbsolutePath());
            } catch (StorageException ex) {
                logger.error(join(" ", file.toString(), ex.getMessage()));
            }
        }
    }

    private File[] filterFilesInRootPath() {
        return rootPath.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(FILE_EXTENSION);
            }
        });
    }

    private KVEntry readEntryFromFile(File file) throws StorageException {
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
            return (KVEntry) stream.readObject();
        } catch (IOException ex) {
            logger.error(ex);
            throw new StorageException(
                    "Entry was not read from persistent storage due to IO error.");
        } catch (ClassNotFoundException | ClassCastException ex) {
            logger.error(ex);
            throw new StorageException(
                    "Entry was not read from persistent storage due to format conversion error.");
        }
    }


}
