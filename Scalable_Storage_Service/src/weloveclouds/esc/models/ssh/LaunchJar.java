package weloveclouds.esc.models.ssh;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.esc.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public class LaunchJar implements IRemoteCommand {
    private StorageNode remoteHost;
    private List<String> arguments;

    public LaunchJar(String jarFilePath) {
        this.arguments = new ArrayList<>();
        this.arguments.add(jarFilePath);
    }

    @Override
    public IAuthenticable on(StorageNode remoteHost) {
        this.remoteHost = remoteHost;
        return new AuthenticableCommandExecutionRequest(this);
    }

    public StorageNode getRemoteHost() {
        return remoteHost;
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }
}