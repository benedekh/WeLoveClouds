package weloveclouds.server.store.persistent;

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

import weloveclouds.kvstore.KVEntry;
import weloveclouds.server.store.IKVStore;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.exceptions.ValueNotFoundException;

public class KVPersistentStorage extends Observable implements IKVStore {

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
    public synchronized void putEntry(KVEntry entry) throws StorageException {
        String key = entry.getKey();

        if (key == null || entry.getValue() == null) {
            throw new StorageException("Key and value cannot be null.");
        } else if (persistentPaths.containsKey(key)) {
            removeEntry(key);
        }

        String cleanKey = key.replaceAll("[^a-zA-Z0-9.-]", "_"); // valid filename
        Path entryPath = Paths.get(rootPath.toString(), join(".", cleanKey, FILE_EXTENSION));

        try (ObjectOutputStream stream =
                new ObjectOutputStream(new FileOutputStream(entryPath.toString()))) {
            stream.writeObject(entry);
        } catch (FileNotFoundException e) {
            throw new StorageException("File was not found.");
        } catch (IOException e) {
            throw new StorageException(
                    "Entry was not saved to the persistent storage due to IO error.");
        }

        persistentPaths.put(key, entryPath);
        notifyObservers(entry);
    }

    @Override
    public synchronized String getValue(String key) throws StorageException, ValueNotFoundException {
        Path path = persistentPaths.get(key);
        if (path == null) {
            throw new ValueNotFoundException(key);
        }
        KVEntry entry = readEntryFromFile(path.toFile());
        return entry.getValue();
    }

    @Override
    public synchronized void removeEntry(String key) throws StorageException {
        try {
            if (persistentPaths.containsKey(key)) {
                Path path = persistentPaths.get(key);
                Files.delete(path);
                persistentPaths.remove(key);
                notifyObservers(key);
            }
        } catch (NullPointerException ex) {
            throw new StorageException("Key cannot be null.");
        } catch (NoSuchFileException ex) {
            persistentPaths.remove(key);
            throw new StorageException("File for key was already removed.");
        } catch (IOException e) {
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
            throw new StorageException(
                    "Entry was not read from persistent storage due to IO error.");
        } catch (ClassNotFoundException | ClassCastException ex) {
            throw new StorageException(
                    "Entry was not read from persistent storage due to format conversion error.");
        }
    }


}
