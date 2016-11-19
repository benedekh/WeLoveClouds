package weloveclouds.kvstore.deserialization.helper;

import weloveclouds.kvstore.serialization.exceptions.DeserializationException;

/**
 * A deserializer that can convert from type E to T.
 * 
 * @author Benoit
 */
public interface IDeserializer<T, E> {
    /**
     * Converts a serialized object to a T type.
     * 
     * @return the T type representation of the source (from) object, or null if target == null
     * 
     * @throws DeserializationException if any error occurs
     */
    T deserialize(E from) throws DeserializationException;
}
