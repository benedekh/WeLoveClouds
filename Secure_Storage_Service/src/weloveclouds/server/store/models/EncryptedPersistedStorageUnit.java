package weloveclouds.server.store.models;

import java.nio.file.Path;
import java.util.Map;

import weloveclouds.commons.utils.encryption.StringEncryptionUtil;
import weloveclouds.commons.utils.encryption.models.EncryptedStringMap;

/**
 * A {@link PersistedStorageUnit} whose keys and values are encrypted.
 * 
 * @author Benedek
 */
public class EncryptedPersistedStorageUnit extends PersistedStorageUnit {

    private static final long serialVersionUID = -1272369371633694784L;

    protected EncryptedPersistedStorageUnit(Map<String, String> initializerMap, Path filePath) {
        super(initializerMap, filePath);
    }

    public EncryptedPersistedStorageUnit(Path filePath, StringEncryptionUtil encryptionUtil) {
        super(filePath);
        entries = new EncryptedStringMap(entries, encryptionUtil);
    }

}
