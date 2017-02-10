package weloveclouds.loadbalancer.services;

/**
 * Created by Benoit on 2017-01-27.
 */
public interface IHealthMonitoringService extends Runnable {
    void start();

    @Override
    void run();
}
