package weloveclouds.ecs.models.commands;


/**
 * Created by Benoit on 2016-11-21.
 */
public interface IValidatable {
    ICommand validate() throws IllegalArgumentException;
}
