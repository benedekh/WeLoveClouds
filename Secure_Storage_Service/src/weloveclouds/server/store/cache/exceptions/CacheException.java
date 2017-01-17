package weloveclouds.server.store.cache.exceptions;

/**
 * Represents an exception that was created in the {@link KVCache}.
 * 
 * @author Benedek
 */
public class CacheException extends Exception {

    private static final long serialVersionUID = 3037481656022380929L;

    public CacheException(String message) {
        super(message);
    }

}
