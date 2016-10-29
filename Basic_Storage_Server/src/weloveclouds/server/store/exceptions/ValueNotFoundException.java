package weloveclouds.server.store.exceptions;

import weloveclouds.client.utils.CustomStringJoiner;

public class ValueNotFoundException extends StorageException {

    private static final long serialVersionUID = -4811231863338933503L;

    public ValueNotFoundException(String key) {
        super(CustomStringJoiner.join(" ", "Value not found for key", key));
    }

}
