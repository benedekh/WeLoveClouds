package weloveclouds.commons.serialization;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

/**
 * A deserializer that can convert from type E to T.
 * 
 * @author Benoit
 */
public interface IDeserializer<T, E> {

    /**
     * Converts a serialized object to a T type.
     * 
     * @return the T type representation of the source object, or null if it is null
     * 
     * @throws DeserializationException if any error occurs
     */
    T deserialize(E from) throws DeserializationException;
}
