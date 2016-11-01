package weloveclouds.kvstore.serialization;

/**
 * Created by Benoit on 2016-11-01.
 */
public interface IMessageSerializer<T, E> {
    T serialize(E unserializedMessage);
}
