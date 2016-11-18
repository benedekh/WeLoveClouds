package weloveclouds.ecs.models.commands;

/**
 * Interface for objects which can be validated.
 * 
 * @author benoit
 *
 */

public interface IValidatable {
    /**
     * Validates the respective command. Returns the command if the validation was successful.
     *
     * @throws IllegalArgumentException if the command's arguments are syntactically or semantically
     *                                  invalid
     */
    
    ICommand validate() throws IllegalArgumentException;
}
