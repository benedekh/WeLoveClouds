package weloveclouds.ecs.core;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.ecs.services.ISecureShellService;
import weloveclouds.kvstore.serialization.IMessageDeserializer;
import weloveclouds.kvstore.serialization.IMessageSerializer;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExternalConfigurationService {
    private IConcurrentCommunicationApi concurrentCommunicationApi;
    private ISecureShellService secureShellService;
    private IMessageSerializer messageSerializer;
    private IMessageDeserializer messageDeserializer;


    public ExternalConfigurationService(ExternalConfigurationServiceBuilder externalConfigurationServiceBuilder){

    }

    void initService(int numberOfNodes, int cacheSize, String displacementStrategy){

    }
    void start(){

    }
    void stop(){

    }
    void shutDown(){

    }
    void addNode(int cacheSize, String displacementStrategy){

    }
    void removeNode(){

    }

    public static class ExternalConfigurationServiceBuilder{
        private IConcurrentCommunicationApi concurrentCommunicationApi;
        private ISecureShellService secureShellService;
        private IMessageSerializer messageSerializer;
        private IMessageDeserializer messageDeserializer;

        ExternalConfigurationServiceBuilder concurrentCommunicationApi
                (IConcurrentCommunicationApi concurrentCommunicationApi){
            this.concurrentCommunicationApi = concurrentCommunicationApi;
            return this;
        }

        ExternalConfigurationServiceBuilder secureShellService(ISecureShellService secureShellService){
            this.secureShellService = secureShellService;
            return this;
        }

        ExternalConfigurationServiceBuilder messageSerializer(IMessageSerializer messageSerializer){
            this.messageSerializer = messageSerializer;
            return this;
        }

        ExternalConfigurationServiceBuilder messageDeserializer(IMessageDeserializer messageDeserializer){
            this.messageDeserializer = messageDeserializer;
            return this;
        }

        ExternalConfigurationService build(){
            return new ExternalConfigurationService(this);
        }
    }
}
