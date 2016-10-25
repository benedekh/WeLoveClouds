package weloveclouds.client.models.commands;

import java.security.InvalidParameterException;

/**
 * Created by Benoit on 2016-10-25.
 */
public interface IValidatable {
    ICommand validate() throws IllegalArgumentException;
}
