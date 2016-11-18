package weloveclouds.ecs.models.ssh;

import java.util.List;

import weloveclouds.ecs.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public interface IRemoteCommand {
    IAuthenticable on(StorageNode remoteHost);

    StorageNode getRemoteHost();

    List<String> getArguments();
}
