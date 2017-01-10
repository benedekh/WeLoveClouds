package weloveclouds.commons.networking.models.requests;

/**
 * Interface for objects which can be validated.
 *
 * @author Benedek
 */
public interface IValidatable<E> {

    /**
     * Validates the respective object. Returns it if the validation was successful.
     *
     * @throws IllegalArgumentException if the object is syntactically or semantically invalid
     */
    E validate() throws IllegalArgumentException;
}
