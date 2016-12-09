package weloveclouds.commons.serialization;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;

/**
 * A deserializer that can convert from type E to T, or from byte[] to T
 * 
 * @author Benoit
 */
public interface IMessageDeserializer<T, E> {
    /**
     * Converts a serialized message to a T type.
     * 
     * @throws DeserializationException if any error occurs
     */
    T deserialize(E serializedMessage) throws DeserializationException;

    /**
     * Converts a serialized message from byte[] to a T type.
     * 
     * @throws DeserializationException if any error occurs
     */
    T deserialize(byte[] serializedMessage) throws DeserializationException;
}
