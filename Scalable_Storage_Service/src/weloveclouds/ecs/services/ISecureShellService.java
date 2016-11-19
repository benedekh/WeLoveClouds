package weloveclouds.ecs.services;

import com.jcraft.jsch.JSchException;

import weloveclouds.ecs.exceptions.ssh.SecureShellServiceException;
import weloveclouds.ecs.models.commands.ssh.AbstractRemoteCommand;
import weloveclouds.ecs.models.ssh.AuthInfos;
import weloveclouds.ecs.models.ssh.RemoteCommandExecutionRequest;

/**
 * Created by Benoit on 2016-11-18.
 */
public interface ISecureShellService {
    void runCommand(AbstractRemoteCommand command) throws SecureShellServiceException;
}
