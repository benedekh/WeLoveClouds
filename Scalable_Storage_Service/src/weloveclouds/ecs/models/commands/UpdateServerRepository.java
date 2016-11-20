package weloveclouds.ecs.models.commands;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.models.repository.ServerRepository;
import weloveclouds.ecs.models.repository.StorageNode;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.IDLE;

/**
 * Created by Benoit on 2016-11-19.
 */
public class UpdateServerRepository extends AbstractCommand<StorageNode> implements ICommand {
    private ServerRepository serverRepository;

    public UpdateServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Override
    public void execute() throws ClientSideException {
        if (!arguments.isEmpty()) {
            for (StorageNode storageNode : arguments) {
                storageNode.setStatus(IDLE);
            }
        }
    }
}
