package weloveclouds.commons.utils.encryption.models;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.encryption.StringEncryptionUtil;
import weloveclouds.commons.utils.encryption.exception.DecryptionException;
import weloveclouds.commons.utils.encryption.exception.EncryptionException;

/**
 * Encapsulates a {@link Map<String, String>} whose keys and values will be encrypted.
 * 
 * @author Benedek
 */
public class EncryptedStringMap implements Map<String, String>, Serializable {

    private static final long serialVersionUID = -4523830764422049366L;
    private static final Logger LOGGER = Logger.getLogger(EncryptedStringMap.class);

    private Map<String, String> encapsulatedMap;
    private transient StringEncryptionUtil encryptionUtil;

    public EncryptedStringMap(Map<String, String> encapsulatedMap,
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
        try {
            return encapsulatedMap.containsValue(encryptionUtil.encrypt(cast(value)));
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    @Override
    public String get(Object key) {
        try {
            String encryptedKey = encryptionUtil.encrypt(cast(key));
            String encryptedValue = encapsulatedMap.get(encryptedKey);
            return encryptionUtil.decrypt(encryptedValue);
        } catch (EncryptionException | DecryptionException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    @Override
    public String put(String key, String value) {
        try {
            String encryptedKey = encryptionUtil.encrypt(key);
            String encryptedValue = encryptionUtil.encrypt(value);
            return encapsulatedMap.put(encryptedKey, encryptedValue);
        } catch (EncryptionException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    @Override
    public String remove(Object key) {
        try {
            String encryptedValue = encapsulatedMap.remove(encryptionUtil.encrypt(cast(key)));
            return encryptionUtil.decrypt(encryptedValue);
        } catch (EncryptionException | DecryptionException ex) {
            LOGGER.error(ex);
            return null;
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        Map<String, String> encrypted = new HashMap<>(m);
        for (Entry<?, ?> entry : m.entrySet()) {
            try {
                String encryptedKey = encryptionUtil.encrypt(cast(entry.getKey()));
                String encryptedValue = encryptionUtil.encrypt(cast(entry.getValue()));
                encrypted.put(encryptedKey, encryptedValue);
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
    public Collection<String> values() {
        List<String> decryptedValues = new ArrayList<>();
        for (String value : encapsulatedMap.values()) {
            try {
                decryptedValues.add(encryptionUtil.decrypt(value));
            } catch (DecryptionException ex) {
                LOGGER.error(ex);
            }
        }
        return decryptedValues;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> decrypted = new HashSet<>();
        for (Entry<String, String> entry : encapsulatedMap.entrySet()) {
            try {
                String decryptedKey = encryptionUtil.decrypt(entry.getKey());
                String decryptedValue = encryptionUtil.decrypt(entry.getValue());
                decrypted.add(new SimpleEntry<String, String>(decryptedKey, decryptedValue));
            } catch (DecryptionException ex) {
                LOGGER.error(ex);
            }
        }
        return decrypted;
    }

    private String cast(Object object) {
        try {
            return (String) object;
        } catch (ClassCastException ex) {
            return null;
        }
    }

}
