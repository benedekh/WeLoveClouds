package weloveclouds.server.store.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    public String toStringWithDelimiter(String betweenStorageUnits, String betweenEntries,
            String withinEntry) {
        StringBuilder sb = new StringBuilder();
        for (MovableStorageUnit storageUnit : storageUnits) {
            sb.append(storageUnit.toStringWithDelimiter(betweenEntries, withinEntry));
            sb.append(betweenStorageUnits);
        }
        sb.setLength(sb.length() - betweenStorageUnits.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(toStringWithDelimiter(",", ";", "::"));
        sb.append("}");
        return sb.toString();
    }

}
