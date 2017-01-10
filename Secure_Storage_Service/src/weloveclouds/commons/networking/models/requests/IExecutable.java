package weloveclouds.commons.networking.models.requests;

/**
 * Represents an executable which produces M typed object.
 * 
 * @author Benedek
 *
 * @param <M> the type of the result object after execution
 */
public interface IExecutable<M> {

    /**
     * @return the result of the execution
     */
    M execute();
}
