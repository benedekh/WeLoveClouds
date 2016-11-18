package weloveclouds.ecs.models.ssh;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.ecs.models.metadata.StorageNode;

/**
 * Created by Benoit on 2016-11-16.
 */
public class LaunchJar implements IRemoteCommand {
    private static final String ARGUMENTS_DELIMITER = " ";
    private static final String COMMAND = "java -jar";
    private StorageNode remoteHost;
    private List<String> jvmArguments;
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
        return this.arguments;
    }

    public LaunchJar withJvmArguments(List<String> jvmArguments) {
        jvmArguments = jvmArguments;
        arguments.addAll(jvmArguments);
        return this;
    }

    public String toString(){
        ArrayList<String> commandAndArguments = new ArrayList<>();
        commandAndArguments.add(COMMAND);
        commandAndArguments.addAll(arguments);
        return CustomStringJoiner.join(ARGUMENTS_DELIMITER, commandAndArguments);
    }
}