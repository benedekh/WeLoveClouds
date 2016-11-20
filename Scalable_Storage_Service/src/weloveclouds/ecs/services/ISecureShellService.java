package weloveclouds.ecs.services;

import weloveclouds.ecs.exceptions.ssh.SecureShellServiceException;
import weloveclouds.ecs.models.commands.internal.ssh.AbstractRemoteCommand;

/**
 * Created by Benoit on 2016-11-18.
 */
public interface ISecureShellService {
    void runCommand(AbstractRemoteCommand command) throws SecureShellServiceException;
}
