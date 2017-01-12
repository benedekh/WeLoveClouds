package weloveclouds.commons.utils.ecryption.models;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.ecryption.StringEncryptionUtil;
import weloveclouds.commons.utils.ecryption.exception.DecryptionException;
import weloveclouds.commons.utils.ecryption.exception.EncryptionException;
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
            return encapsulatedMap.put(encryptedKey, value);
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
        try {
            Map<String, PersistedStorageUnit> encrypted = new HashMap<>(m);
            for (Entry<?, ? extends PersistedStorageUnit> entry : m.entrySet()) {
                String encryptedKey = encryptionUtil.encrypt(cast(entry.getKey()));
                encrypted.put(encryptedKey, entry.getValue());
            }
            encapsulatedMap.putAll(encrypted);
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public void clear() {
        encapsulatedMap.clear();
    }

    @Override
    public Set<String> keySet() {
        try {
            Set<String> decryptedKeys = new HashSet<>();
            for (String key : encapsulatedMap.keySet()) {
                decryptedKeys.add(encryptionUtil.decrypt(key));
            }
            return decryptedKeys;
        } catch (DecryptionException ex) {
            LOGGER.error(ex);
            return new HashSet<>();
        }
    }

    @Override
    public Collection<PersistedStorageUnit> values() {
        return encapsulatedMap.values();
    }

    @Override
    public Set<Entry<String, PersistedStorageUnit>> entrySet() {
        try {
            Set<Entry<String, PersistedStorageUnit>> decrypted = new HashSet<>();
            for (Entry<String, PersistedStorageUnit> entry : encapsulatedMap.entrySet()) {
                String decryptedKey = encryptionUtil.decrypt(entry.getKey());
                decrypted.add(new SimpleEntry<String, PersistedStorageUnit>(decryptedKey,
                        entry.getValue()));
            }
            return decrypted;
        } catch (DecryptionException ex) {
            LOGGER.error(ex);
            return new HashSet<>();
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
