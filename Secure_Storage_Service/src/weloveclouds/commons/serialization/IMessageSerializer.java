package weloveclouds.commons.serialization;

/**
 * A serializer that can convert from type E to T.
 * 
 * @author Benoit
 */
public interface IMessageSerializer<T, E> {
    /**
     * Converts an unserialized message to a T type.
     */
    T serialize(E unserializedMessage);
}
