package weloveclouds.commons.utils.encryption.models;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.utils.encryption.StringEncryptionUtil;
import weloveclouds.commons.utils.encryption.exception.DecryptionException;
import weloveclouds.commons.utils.encryption.exception.EncryptionException;
import weloveclouds.server.store.exceptions.StorageException;
import weloveclouds.server.store.models.EncryptedPersistedStorageUnit;
import weloveclouds.server.store.models.PersistedStorageUnit;

/**
 * Encapsulates a {@link Map<String, PersistedStorageUnit>} whose keys will be encrypted.
 * 
 * @author Benedek
 */
public class EncryptedStorageUnitMap implements Map<String, PersistedStorageUnit> {

    private static final Logger LOGGER = Logger.getLogger(EncryptedStorageUnitMap.class);

    private Map<String, PersistedStorageUnit> encapsulatedMap;
    private StringEncryptionUtil encryptionUtil;

    public EncryptedStorageUnitMap(Map<String, PersistedStorageUnit> encapsulatedMap,
            StringEncryptionUtil encryptionUtil) {
        this.encapsulatedMap = encapsulatedMap;
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public int size() {
        return encapsulatedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return encapsulatedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return encapsulatedMap.containsKey(encryptionUtil.encrypt(cast(key)));
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return encapsulatedMap.containsValue(value);
    }

    @Override
    public PersistedStorageUnit get(Object key) {
        try {
            String encryptedKey = encryptionUtil.encrypt(cast(key));
            return encapsulatedMap.get(encryptedKey);
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    @Override
    public PersistedStorageUnit put(String key, PersistedStorageUnit value) {
        try {
            String encryptedKey = encryptionUtil.encrypt(key);
            PersistedStorageUnit encryptedUnit = encryptIfNotEncrypted(value);
            return encapsulatedMap.put(encryptedKey, encryptedUnit);
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    @Override
    public PersistedStorageUnit remove(Object key) {
        try {
            return encapsulatedMap.remove(encryptionUtil.encrypt(cast(key)));
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends PersistedStorageUnit> m) {
        Map<String, PersistedStorageUnit> encrypted = new HashMap<>(m);
        for (Entry<?, ? extends PersistedStorageUnit> entry : m.entrySet()) {
            try {
                String encryptedKey = encryptionUtil.encrypt(cast(entry.getKey()));
                encrypted.put(encryptedKey, entry.getValue());
            } catch (EncryptionException ex) {
                LOGGER.error(ex);
            }
        }
        encapsulatedMap.putAll(encrypted);
    }

    @Override
    public void clear() {
        encapsulatedMap.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> decryptedKeys = new HashSet<>();
        for (String key : encapsulatedMap.keySet()) {
            try {
                decryptedKeys.add(encryptionUtil.decrypt(key));
            } catch (DecryptionException ex) {
                LOGGER.error(ex);
            }
        }
        return decryptedKeys;
    }

    @Override
    public Collection<PersistedStorageUnit> values() {
        return encapsulatedMap.values();
    }

    @Override
    public Set<Entry<String, PersistedStorageUnit>> entrySet() {
        Set<Entry<String, PersistedStorageUnit>> decrypted = new HashSet<>();
        for (Entry<String, PersistedStorageUnit> entry : encapsulatedMap.entrySet()) {
            try {
                String decryptedKey = encryptionUtil.decrypt(entry.getKey());
                decrypted.add(new SimpleEntry<String, PersistedStorageUnit>(decryptedKey,
                        entry.getValue()));
            } catch (DecryptionException ex) {
                LOGGER.error(ex);
            }
        }
        return decrypted;
    }

    private PersistedStorageUnit encryptIfNotEncrypted(PersistedStorageUnit storageUnit) {
        if (!(storageUnit instanceof EncryptedPersistedStorageUnit)) {
            EncryptedPersistedStorageUnit encryptedUnit =
                    new EncryptedPersistedStorageUnit(storageUnit.getPath(), encryptionUtil);
            for (String key : storageUnit.getKeys()) {
                try {
                    String value = storageUnit.getValue(key);
                    String encryptedKey = encryptionUtil.encrypt(key);
                    String encryptedValue = encryptionUtil.encrypt(value);
                    encryptedUnit.putEntry(new KVEntry(encryptedKey, encryptedValue));
                } catch (EncryptionException | StorageException ex) {
                    LOGGER.error(ex);
                }
            }
            try {
                encryptedUnit.save();
            } catch (StorageException ex) {
                LOGGER.error(ex);
            }
            return encryptedUnit;
        } else {
            return storageUnit;
        }
    }

    private String cast(Object object) {
        try {
            return (String) object;
        } catch (ClassCastException ex) {
            return null;
        }
    }

}
