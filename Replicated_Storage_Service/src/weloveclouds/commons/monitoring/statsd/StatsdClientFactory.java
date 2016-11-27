package weloveclouds.commons.monitoring.statsd;

import com.timgroup.statsd.NonBlockingStatsDClient;

import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.commons.monitoring.statsd.configuration.StatsdConfigurationProvider;

import static weloveclouds.commons.context.Environment.DEVELOPMENT;
import static weloveclouds.commons.context.Environment.PRODUCTION;


/**
 * Created by Benoit on 2016-11-27.
 */
public class StatsdClientFactory {
    private static StatsdConfigurationProvider statsdConfigurationProvider =
            StatsdConfigurationProvider.getInstance();

    public static IStatsdClient createStatdClientFromEnvironment() {
        IStatsdClient statsdClient;

        switch (ExecutionContext.getExecutionEnvironment()) {
            case PRODUCTION:
                statsdClient = new SimpleStatsdClient(new NonBlockingStatsDClient(PRODUCTION
                        .toString(), statsdConfigurationProvider.getStatsdServerAddress(),
                        statsdConfigurationProvider.getStatsdServicePort()));
                break;
            case DEVELOPMENT:
                statsdClient = new SimpleStatsdClient(new NonBlockingStatsDClient(DEVELOPMENT
                        .toString(), statsdConfigurationProvider.getStatsdServerAddress(),
                        statsdConfigurationProvider.getStatsdServicePort()));
                break;
            default:
                statsdClient = new DummyStatsdClient();
                break;
        }
        return statsdClient;
    }
}
