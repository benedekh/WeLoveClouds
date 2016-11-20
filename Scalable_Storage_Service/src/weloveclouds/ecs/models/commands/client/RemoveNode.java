package weloveclouds.ecs.models.commands.client;

import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.commands.AbstractCommand;

/**
 * Created by Benoit on 2016-11-20.
 */
public class RemoveNode extends AbstractEcsClientCommand {
    public RemoveNode(IKVEcsApi externalCommunicationServiceApi) {
        super(externalCommunicationServiceApi);
    }

    @Override
    public void execute() throws ClientSideException {
        externalCommunicationServiceApi.removeNode();
    }

    @Override
    public String toString() {
        return null;
    }
}
