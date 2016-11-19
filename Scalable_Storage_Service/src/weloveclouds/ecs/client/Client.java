package weloveclouds.ecs.client;

import weloveclouds.ecs.models.commands.EcsCommandFactory;

/**
 * Created by Benoit on 2016-11-16.
 */
public class Client {
    private EcsCommandFactory ecsCommandFactory;

    public Client(EcsClientBuilder escClientBuilder) {
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

        public Client build() {
            return new Client(this);
        }
    }
}
