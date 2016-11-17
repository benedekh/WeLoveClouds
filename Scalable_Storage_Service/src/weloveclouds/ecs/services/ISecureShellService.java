package weloveclouds.ecs.services;

import weloveclouds.ecs.models.ssh.IExecutableRemoteCommandRequest;

/**
 * Created by Benoit on 2016-11-16.
 */
public interface ISecureShellService {
    void execute(IExecutableRemoteCommandRequest executable);
}
