package weloveclouds.server.store;

import weloveclouds.kvstore.KVEntry;

public interface IEntryChangeNotifyable {
    
    public void put(KVEntry entry);
    public void remove(String key);
}
