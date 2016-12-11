package weloveclouds.loadbalancer.services;

/**
 * Created by Benoit on 2016-12-06.
 */
public interface INotifier<T> {
    void notify(T notification);
}
