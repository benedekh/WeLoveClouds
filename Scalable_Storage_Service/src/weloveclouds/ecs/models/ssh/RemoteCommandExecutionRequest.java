package weloveclouds.ecs.models.ssh;

import weloveclouds.ecs.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public class RemoteCommandExecutionRequest {
    AuthenticableCommandExecutionRequest authenticableCommandExecutionRequest;

    public RemoteCommandExecutionRequest(AuthenticableCommandExecutionRequest authenticableCommandExecutionRequest) {
        this.authenticableCommandExecutionRequest = authenticableCommandExecutionRequest;
    }

    public IRemoteCommand getCommand() {
        return authenticableCommandExecutionRequest.getCommand();
    }

    public AuthInfos getAuthInfos() {
        return authenticableCommandExecutionRequest.getAuthInfos();
    }

    public StorageNode getRemoteHost() {
        return authenticableCommandExecutionRequest.getRemoteHost();
    }
}
