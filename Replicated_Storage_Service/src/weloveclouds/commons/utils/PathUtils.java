package weloveclouds.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Utility class to make file operations (save to file, load from file, delete file) handier.
 * 
 * @author Benedek
 */
public class PathUtils {

    /**
     * Saves the parameter object into the file referred by the respective path.
     * 
     * @throws IOException if any error occurs
     */
    public static <T extends Serializable> void saveToFile(Path path, T toBeSaved)
            throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(path))) {
            stream.writeObject(toBeSaved);
        }
    }

    /**
     * Loads the object that was saved in the file referred by the path.
     * 
     * @throws IOException if any error occurs
     * @throws ClassNotFoundException if the object's type is different from the one that has been
     *         set as return type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T loadFromFile(Path path)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(path))) {
            return (T) stream.readObject();
        }
    }

    /**
     * Removes the file denoted by its path.
     * 
     * @throws IOException if any error occurs
     */
    public static void deleteFile(Path path) throws IOException {
        Files.delete(path);
    }

    /**
     * Creates a dummy, but valid path to a temp file in the temp folder.
     * 
     * @throws IOException if an error occurs
     */
    public static Path createDummyPath() throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".ser");
        return tempFile.toPath();
    }

    /**
     * Generates a unique file path based on the basePath.
     * 
     * @param extension the file extension in the end of the path
     */
    public static Path generateUniqueFilePath(Path basePath, String extension) {
        Path path = generateFilePath(basePath, extension);
        while (exists(path)) {
            path = generateFilePath(basePath, extension);
        }
        return path;
    }

    /**
     * Checks if the path exists.
     */
    private static boolean exists(Path path) {
        return path.toAbsolutePath().toFile().exists();
    }

    /**
     * Generates a file path based on the basePath.
     * 
     * @param extension the file extension in the end of the path
     */
    private static Path generateFilePath(Path basePath, String extension) {
        String filename = UUID.randomUUID().toString();
        return Paths.get(basePath.toString(), StringUtils.join(".", filename, extension));
    }

}
