package weloveclouds.esc.models.ssh;

import weloveclouds.esc.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public class ExecutableRemoteCommandExecutionRequest implements IExecutableRemoteCommandRequest {
    AuthenticableCommandExecutionRequest authenticableCommandExecutionRequest;

    public ExecutableRemoteCommandExecutionRequest(AuthenticableCommandExecutionRequest authenticableCommandExecutionRequest) {
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
