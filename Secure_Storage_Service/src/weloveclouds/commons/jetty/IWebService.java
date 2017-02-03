package weloveclouds.commons.jetty;

/**
 * Created by Benoit on 2017-01-27.
 */
public interface IWebService extends Runnable {
    void start();
    void run();
}
