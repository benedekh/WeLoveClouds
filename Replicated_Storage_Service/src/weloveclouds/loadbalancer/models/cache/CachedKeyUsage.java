package weloveclouds.loadbalancer.models.cache;

/**
 * Created by Benoit on 2016-12-06.
 */
public class CachedKeyUsage<K> {
    private K key;
    private Integer numberOfOperationPerformedOnKey;

    public CachedKeyUsage(K key) {
        this.key = key;
        this.numberOfOperationPerformedOnKey = 0;
    }

    public K getKey() {
        return key;
    }

    public Integer getNumberOfOperationPerformedOnKey() {
        return numberOfOperationPerformedOnKey;
    }

    public void incrementNumberOfOperationPerformed() {
        numberOfOperationPerformedOnKey++;
    }
}
