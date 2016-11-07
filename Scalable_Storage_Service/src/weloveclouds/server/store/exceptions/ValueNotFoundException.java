package weloveclouds.server.store.exceptions;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.server.services.DataAccessService;

/**
 * Thrown if no value was stored in {@link DataAccessService} for the respective key.
 * 
 * @author Benedek
 */
public class ValueNotFoundException extends StorageException {

    private static final long serialVersionUID = -4811231863338933503L;

    public ValueNotFoundException(String key) {
        super(CustomStringJoiner.join(" ", "Value not found for key", key));
    }

}
