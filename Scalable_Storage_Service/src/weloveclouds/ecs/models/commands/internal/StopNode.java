package weloveclouds.ecs.models.commands.internal;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.commands.AbstractCommand;
import weloveclouds.ecs.models.repository.StorageNode;

/**
 * Created by Benoit on 2016-11-20.
 */
public class StopNode extends AbstractCommand<StorageNode> {
    private IConcurrentCommunicationApi concurrentCommunicationApi;

    public StopNode(IConcurrentCommunicationApi concurrentCommunicationApi, StorageNode node) {
        this.concurrentCommunicationApi = concurrentCommunicationApi;
        this.addArgument(node);
    }

    @Override
    public void execute() throws ClientSideException {

    }

    @Override
    public String toString() {
        return CustomStringJoiner.join(" ", "StopNode");
    }
}
