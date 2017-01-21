package weloveclouds.server.store.models;

/**
 * Represents if the put command was an insert (new add to the storage) or an update (key was
 * already stored in the key-value storage).
 * 
 * @author Benedek
 */
public enum PutType {

    INSERT, UPDATE;

}
