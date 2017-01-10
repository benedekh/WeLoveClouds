package weloveclouds.commons.kvstore.models;

import java.io.Serializable;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a Key-value pair that is transferred through the network.
 * 
 * @author Benedek
 */
public class KVEntry implements Serializable {

    private static final long serialVersionUID = 3871522756840647625L;

    private String key;
    private String value;

    public KVEntry() {
        this.key = null;
        this.value = null;
    }

    public KVEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KVEntry other = (KVEntry) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public String toStringWithDelimiter(String delimiter) {
        return StringUtils.join(delimiter, key, value);
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "{", toStringWithDelimiter("::"), "}");
    }

}
