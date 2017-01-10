package weloveclouds.commons.monitoring.statsd;

import com.timgroup.statsd.NonBlockingStatsDClient;

import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.monitoring.statsd.configuration.StatsdConfigurationProvider;

import static weloveclouds.commons.context.Environment.DEVELOPMENT;
import static weloveclouds.commons.context.Environment.PRODUCTION;


/**
 * A factory which creates the respective statsd client.
 * 
 * @author Benoit
 */
public class StatsdClientFactory {
    private static StatsdConfigurationProvider statsdConfigurationProvider =
            StatsdConfigurationProvider.getInstance();

    /**
     * Creates a statsd client based on the execution context
     * ({@link ExecutionContext#getExecutionEnvironment()}.
     */
    public static IStatsdClient createStatdClientFromEnvironment() {
        IStatsdClient statsdClient;

        switch (ExecutionContext.getExecutionEnvironment()) {
            case PRODUCTION:
                statsdClient =
                        new SimpleStatsdClient(new NonBlockingStatsDClient(PRODUCTION.toString(),
                                statsdConfigurationProvider.getStatsdServerAddress(),
                                statsdConfigurationProvider.getStatsdServicePort()));
                break;
            case DEVELOPMENT:
                statsdClient =
                        new SimpleStatsdClient(new NonBlockingStatsDClient(DEVELOPMENT.toString(),
                                statsdConfigurationProvider.getStatsdServerAddress(),
                                statsdConfigurationProvider.getStatsdServicePort()));
                break;
            default:
                statsdClient = new DummyStatsdClient();
                break;
        }
        return statsdClient;
    }
}
