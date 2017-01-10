package weloveclouds.commons.kvstore.models;

/**
 * Represents a request between the server and the data access layer to the persistent storage /
 * cache.
 * 
 * @author Benoit
 */
public interface IKVRequest {
    /**
     * Executes the request.
     */
    void execute();
}
