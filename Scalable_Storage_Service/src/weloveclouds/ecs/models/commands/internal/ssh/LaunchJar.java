
package weloveclouds.ecs.models.commands.internal.ssh;

import java.util.List;

import weloveclouds.ecs.exceptions.ssh.SecureShellServiceException;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.services.ISecureShellService;

import static weloveclouds.ecs.models.repository.StorageNodeStatus.INITIALIZED;

/**
 * Created by Benoit on 2016-11-16.
 */
public class LaunchJar extends AbstractRemoteCommand {
    private static final String COMMAND = "java -jar";
    private String jarFilePath;
    private ISecureShellService secureShellService;

    public LaunchJar(Builder commandBuilder) {
        super(COMMAND, commandBuilder.arguments, commandBuilder.targettedNode);
        this.secureShellService = commandBuilder.secureShellService;
        this.jarFilePath = commandBuilder.jarFilePath;
    }

    @Override
    public void execute() throws SecureShellServiceException {
        secureShellService.runCommand(this);
        targetedNode.setStatus(INITIALIZED);
    }

    public static class Builder {
        private String jarFilePath;
        private List<String> arguments;
        private StorageNode targettedNode;
        private ISecureShellService secureShellService;

        public Builder jarFilePath(String jarFilePath) {
            this.jarFilePath = jarFilePath;
            return this;
        }

        public Builder arguments(List<String> arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder targettedNode(StorageNode targettedNode) {
            this.targettedNode = targettedNode;
            return this;
        }

        public Builder secureShellService(ISecureShellService secureShellService) {
            this.secureShellService = secureShellService;
            return this;
        }

        public LaunchJar build() {
            return new LaunchJar(this);
        }
    }
}