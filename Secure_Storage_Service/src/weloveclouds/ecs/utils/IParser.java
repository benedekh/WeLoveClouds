package weloveclouds.ecs.utils;

/**
 * Created by Benoit on 2016-11-16.
 */
public interface IParser<E, T> {

    E parse(T toParse);
}
