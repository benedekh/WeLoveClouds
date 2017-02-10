package weloveclouds.ecs.configuration.modules;

import com.google.inject.AbstractModule;

import org.joda.time.Duration;

import weloveclouds.commons.configuration.annotations.MinimumIntervalBetweenRetry;
import weloveclouds.commons.retryer.ExponentialBackoffIntervalComputer;
import weloveclouds.commons.retryer.IBackoffIntervalComputer;
import weloveclouds.ecs.configuration.providers.TaskServiceConfigurationProvider;

/**
 * Created by Benoit on 2017-01-27.
 */
public class TaskServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Duration.class).annotatedWith(MinimumIntervalBetweenRetry.class)
                .toInstance(TaskServiceConfigurationProvider.getMinimumIntervalBetweenRetry());
        bind(IBackoffIntervalComputer.class).to(ExponentialBackoffIntervalComputer.class);
    }
}
