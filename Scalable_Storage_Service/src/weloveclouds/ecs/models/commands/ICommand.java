package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.models.commands.IValidatable;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
* Created by Benoit on 2016-11-18.
*/
public interface ICommand extends IValidatable {

    void execute() throws ClientSideException;
}