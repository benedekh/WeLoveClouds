package weloveclouds.ecs.models.commands;

/**
 * 
 * @author benoit
 *
 */

public interface IValidatable {

    
    ICommand validate() throws IllegalArgumentException;
}
