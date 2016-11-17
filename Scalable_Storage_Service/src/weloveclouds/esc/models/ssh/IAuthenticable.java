package weloveclouds.esc.models.ssh;

import weloveclouds.esc.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public interface IAuthenticable {
    IExecutableRemoteCommandRequest withAuthInfos(AuthInfos authInfos);

    public IRemoteCommand getCommand();

    public AuthInfos getAuthInfos();

    public StorageNode getRemoteHost();
}
