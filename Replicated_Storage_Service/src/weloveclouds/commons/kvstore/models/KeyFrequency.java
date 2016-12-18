package weloveclouds.commons.kvstore.models;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents a class which counts how many times the referred key was accessed.
 *
 * @author Benedek
 */
public class KeyFrequency implements Comparable<KeyFrequency> {

    private String key;
    private int frequency;

    public KeyFrequency(String key, int frequency) {
        this.key = key;
        this.frequency = frequency;
    }

    public void increaseFrequencyByOne() {
        frequency += 1;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + frequency;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        KeyFrequency other = (KeyFrequency) obj;
        if (frequency != other.frequency)
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    @Override
    public int compareTo(KeyFrequency other) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        // compare only on frequency
        if (frequency < other.frequency) {
            return BEFORE;
        } else if (frequency == other.frequency) {
            return EQUAL;
        } else {
            return AFTER;
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(" ", "{", key, ": frequency ->", frequency, "}");
    }

}
