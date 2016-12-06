package weloveclouds.loadbalancer.models.cache;

/**
 * Created by Benoit on 2016-12-06.
 */
public class CachedKeyUsage<K> {
    private K key;
    private Integer numberOfOperationPerformed;

    public CachedKeyUsage(K key) {
        this.key = key;
        this.numberOfOperationPerformed = 0;
    }

    public K getKey() {
        return key;
    }

    public Integer getNumberOfOperationPerformed() {
        return numberOfOperationPerformed;
    }

    public void incrementNumberOfOperationPerformed() {
        numberOfOperationPerformed++;
    }
}
