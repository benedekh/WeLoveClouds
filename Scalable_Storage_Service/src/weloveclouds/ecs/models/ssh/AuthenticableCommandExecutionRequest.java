package weloveclouds.ecs.models.ssh;

import weloveclouds.ecs.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public class AuthenticableCommandExecutionRequest<E> implements IAuthenticable {
    private IRemoteCommand command;
    private AuthInfos authInfos;

    public AuthenticableCommandExecutionRequest(IRemoteCommand command) {
        this.command = command;
    }

    @Override
    public IExecutableRemoteCommandRequest withAuthInfos(AuthInfos authInfos) {
        this.authInfos = authInfos;
        return new ExecutableRemoteCommandExecutionRequest(this);
    }

    public IRemoteCommand getCommand() {
        return command;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public StorageNode getRemoteHost() {
        return command.getRemoteHost();
    }
}
