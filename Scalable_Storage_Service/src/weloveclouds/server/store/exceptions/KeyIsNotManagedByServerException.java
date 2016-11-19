package weloveclouds.server.store.exceptions;

public class KeyIsNotManagedByServerException extends StorageException {

    private static final long serialVersionUID = 6417515944143203526L;

    public KeyIsNotManagedByServerException(String serializedRingMetadata) {
        super(serializedRingMetadata);
    }

}
