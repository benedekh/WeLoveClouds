package weloveclouds.server.store.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import weloveclouds.kvstore.serialization.helper.ISerializer;

/**
 * Stores several {@link MovableStorageUnit}s.
 * 
 * @author Benedek
 */
public class MovableStorageUnits {

    private Set<MovableStorageUnit> storageUnits;

    public MovableStorageUnits() {
        this.storageUnits = new HashSet<>();
    }

    public MovableStorageUnits(Set<MovableStorageUnit> storageUnits) {
        this.storageUnits = storageUnits;
    }

    public void addStorageUnit(MovableStorageUnit unit) {
        storageUnits.add(unit);
    }

    public void removeStorageUnit(MovableStorageUnit unit) {
        storageUnits.remove(unit);
    }

    public Set<MovableStorageUnit> getStorageUnits() {
        return Collections.unmodifiableSet(storageUnits);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((storageUnits == null) ? 0 : storageUnits.hashCode());
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
        if (!(obj instanceof MovableStorageUnits)) {
            return false;
        }
        MovableStorageUnits other = (MovableStorageUnits) obj;
        if (storageUnits == null) {
            if (other.storageUnits != null) {
                return false;
            }
        } else if (!storageUnits.equals(other.storageUnits)) {
            return false;
        }
        return true;
    }

    /**
     * Converts the object to String.
     * 
     * @param betweenStorageUnits separator character among the storage units
     * @param storageUnitSerializer ot convert the {@link MovableStorageUnit} into a String
     *        representation
     */
    public String toStringWithDelimiter(String betweenStorageUnits,
            ISerializer<String, MovableStorageUnit> storageUnitSerializer) {
        StringBuilder sb = new StringBuilder();
        for (MovableStorageUnit storageUnit : storageUnits) {
            sb.append(storageUnitSerializer.serialize(storageUnit));
            sb.append(betweenStorageUnits);
        }
        sb.setLength(sb.length() - betweenStorageUnits.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        ISerializer<String, MovableStorageUnit> defaultSerializer =
                new ISerializer<String, MovableStorageUnit>() {
                    @Override
                    public String serialize(MovableStorageUnit target) {
                        return target.toString();
                    }
                };

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String delimiter = ", ";
        sb.append(toStringWithDelimiter(delimiter, defaultSerializer));
        sb.append("}");
        return sb.toString();
    }

}
