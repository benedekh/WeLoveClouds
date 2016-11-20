package weloveclouds.ecs.models.commands.client;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.exceptions.ClientSideException;

/**
 * Created by Benoit on 2016-11-20.
 */
public class Start extends AbstractEcsClientCommand {
    public Start(IKVEcsApi externalCommunicationServiceApi) {
        super(externalCommunicationServiceApi);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.start();
    }

    @Override
    public String toString() {
        return null;
    }
}
