package weloveclouds.server.store;

/**
 * A common interface to notify about the different changes in the storage.
 * 
 * @author Benedek
 */
public interface IKVStoreNotification {

    /**
     * The respective key was put in the storage.
     */
    void put(String key);

    /**
     * The respective key was get from the storage.
     */
    void get(String key);

    /**
     * The respective key was removed from the storage.
     */
    void remove(String key);

}
