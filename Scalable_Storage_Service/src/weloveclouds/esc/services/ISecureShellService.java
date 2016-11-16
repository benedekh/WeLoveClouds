package weloveclouds.esc.services;

import weloveclouds.esc.models.ssh.IExecutableRemoteCommandRequest;

/**
 * Created by Benoit on 2016-11-16.
 */
public interface ISecureShellService {
    void execute(IExecutableRemoteCommandRequest executable);
}
