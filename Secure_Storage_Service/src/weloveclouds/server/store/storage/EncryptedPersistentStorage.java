package weloveclouds.server.store.storage;

import java.nio.file.Path;

import weloveclouds.commons.utils.PathUtils;
import weloveclouds.commons.utils.encryption.StringEncryptionUtil;
import weloveclouds.commons.utils.encryption.models.EncryptedStorageUnitMap;
import weloveclouds.server.store.models.EncryptedPersistedStorageUnit;
import weloveclouds.server.store.models.PersistedStorageUnit;

/**
 * A {@link MovablePersistentStorage} whose keys are encrypted.
 * 
 * @author Benedek
 */
public class EncryptedPersistentStorage extends MovablePersistentStorage {

    private StringEncryptionUtil encryptionUtil;

    public EncryptedPersistentStorage(Path rootPath, StringEncryptionUtil encryptionUtil)
            throws IllegalArgumentException {
        super(rootPath);
        super.storageUnits = new EncryptedStorageUnitMap(super.storageUnits, encryptionUtil);
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    protected PersistedStorageUnit createNewStorageUnit() {
        Path path = PathUtils.generateUniqueFilePath(rootPath, FILE_EXTENSION);
        return new EncryptedPersistedStorageUnit(path, encryptionUtil);
    }

}
