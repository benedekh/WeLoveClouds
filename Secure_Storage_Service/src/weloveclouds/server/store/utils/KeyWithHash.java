package weloveclouds.server.store.utils;

import java.io.Serializable;

import weloveclouds.commons.hashing.models.Hash;
import weloveclouds.commons.hashing.utils.HashingUtils;

public class KeyWithHash implements Comparable<KeyWithHash>, Serializable {

    private static final long serialVersionUID = -837230084986545189L;

    private String key;
    private Hash hash;

    public KeyWithHash(String key) {
        this.key = key;
        this.hash = HashingUtils.getHash(key);
    }

    public KeyWithHash(Hash hash) {
        this.hash = hash;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(KeyWithHash o) {
        return hash.compareTo(o.hash);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof KeyWithHash)) {
            return false;
        }
        KeyWithHash other = (KeyWithHash) obj;
        if (hash == null) {
            if (other.hash != null) {
                return false;
            }
        } else if (!hash.equals(other.hash)) {
            return false;
        }
        return true;
    }

}
