package weloveclouds.ecs.services;

import com.jcraft.jsch.JSchException;

import weloveclouds.ecs.models.ssh.RemoteCommandExecutionRequest;

/**
 * Created by Benoit on 2016-11-18.
 */
public interface ISecureShellService {
    void execute(RemoteCommandExecutionRequest remoteCommandExecutionRequest) throws Exception;
}
