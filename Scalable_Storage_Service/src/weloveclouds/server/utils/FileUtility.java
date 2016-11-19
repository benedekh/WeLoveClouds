package weloveclouds.server.utils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class to make file operations (save to file, load from file, delete file) handier.
 * 
 * @author Benedek
 */
public class FileUtility {

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
     * Creates a dummy, but valid path which locates the folder of this ({@link FileUtility}) class.
     */
    public static Path createDummyPath() {
        return new File(
                FileUtility.class.getProtectionDomain().getCodeSource().getLocation().getFile())
                        .toPath();
    }

}
