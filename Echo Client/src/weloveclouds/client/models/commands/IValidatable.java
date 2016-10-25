package weloveclouds.client.models.commands;

/**
 * Created by Benoit on 2016-10-25.
 */
public interface IValidatable {
    ICommand validate() throws IllegalArgumentException;
}
