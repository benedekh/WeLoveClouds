package weloveclouds.server.store;

public interface IKVStoreNotification {

    public void put(String key);

    public void get(String key);

    public void remove(String key);

}
