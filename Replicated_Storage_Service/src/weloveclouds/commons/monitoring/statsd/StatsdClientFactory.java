package weloveclouds.commons.monitoring.statsd;

import com.timgroup.statsd.NonBlockingStatsDClient;

import weloveclouds.commons.monitoring.models.Environment;
import weloveclouds.commons.monitoring.statsd.configuration.StatsdConfigurationProvider;

import static weloveclouds.commons.monitoring.models.Environment.DEVELOPMENT;
import static weloveclouds.commons.monitoring.models.Environment.PRODUCTION;


/**
 * Created by Benoit on 2016-11-27.
 */
public class StatsdClientFactory {
    StatsdConfigurationProvider statsdConfigurationProvider;

    public StatsdClientFactory() {
        this.statsdConfigurationProvider = StatsdConfigurationProvider.getInstance();
    }

    public IStatsdClient StatsdClientcreateStatdClientFromEnvironment(Environment environment) {
        IStatsdClient statsdClient;

        switch (environment) {
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
