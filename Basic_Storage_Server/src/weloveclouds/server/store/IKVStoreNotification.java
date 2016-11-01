package weloveclouds.server.store;

public interface IKVStoreNotification {

    void put(String key);

    void get(String key);

    void remove(String key);

}
