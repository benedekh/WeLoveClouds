package weloveclouds.ecs.client;

import weloveclouds.ecs.models.commands.EcsCommandFactory;

/**
 * Created by Benoit on 2016-11-16.
 */
public class EcsClient {
    private EcsCommandFactory ecsCommandFactory;

    public EcsClient(EcsClientBuilder escClientBuilder) {
        this.ecsCommandFactory = escClientBuilder.ecsCommandFactory;
    }

    public void run(){
        while(!Thread.currentThread().isInterrupted()){

        }
    }

    public static class EcsClientBuilder {
        private EcsCommandFactory ecsCommandFactory;

        public EcsClientBuilder ecs(EcsCommandFactory externalConfigurationService) {
            this.ecsCommandFactory = externalConfigurationService;
            return this;
        }

        public EcsClient build() {
            return new EcsClient(this);
        }
    }
}
