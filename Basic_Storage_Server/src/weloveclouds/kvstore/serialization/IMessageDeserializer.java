package weloveclouds.kvstore.serialization;

import weloveclouds.kvstore.serialization.exceptions.DeserializationException;

/**
 * Created by Benoit on 2016-11-01.
 */
public interface IMessageDeserializer<T, E> {
    T deserialize(E serializedMessage) throws DeserializationException;

    T deserialize(byte[] serializedMessage) throws DeserializationException;
}
