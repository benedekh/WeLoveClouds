package weloveclouds.ecs.models.commands;

public interface IValidatable {

    
    ICommand validate() throws IllegalArgumentException;
}
