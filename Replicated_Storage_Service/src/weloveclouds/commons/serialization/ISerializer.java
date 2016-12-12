package weloveclouds.commons.serialization;

/**
 * That can serialize from type E to T.
 * 
 * @author Benoit
 */
public interface ISerializer<T, E> {

    /**
     * Serializes an object to a T type.
     * 
     * @return the T type representation of the target object, or null if it is null
     */
    T serialize(E target);
}
