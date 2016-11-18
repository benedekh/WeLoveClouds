package weloveclouds.ecs.services;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

import weloveclouds.ecs.models.metadata.StorageNode;
import weloveclouds.ecs.models.ssh.AuthInfos;
import weloveclouds.ecs.models.ssh.RemoteCommandExecutionRequest;

import static weloveclouds.ecs.models.ssh.AuthenticationMethod.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class JshSecureShellService implements ISecureShellService{
    private static final int SECURE_SHELL_PORT = 22;
    private static final String EXEC_CHANNEL = "exec";
    private JSch secureShell;

    public JshSecureShellService() {
        this.secureShell = new JSch();
    }

    public void execute(RemoteCommandExecutionRequest remoteCommandExecutionRequest) throws JSchException {
        StorageNode host = remoteCommandExecutionRequest.getRemoteHost();
        AuthInfos authInfos = remoteCommandExecutionRequest.getAuthInfos();
        Session secureShellSession;

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        if (authInfos.getAuthenticationMethod() == PRIVATE_KEY) {
            secureShell.addIdentity(authInfos.getPrivateKey());
            secureShellSession = secureShell.getSession(authInfos.getUsername(), host.getIpAddress(),
                    SECURE_SHELL_PORT);
        } else {
            secureShellSession = secureShell.getSession(authInfos.getUsername(), host.getIpAddress(),
                    SECURE_SHELL_PORT);
            secureShellSession.setPassword(authInfos.getPassword());
        }

        secureShellSession.setConfig(config);

        secureShellSession.connect();
        ChannelExec channel = (ChannelExec) secureShellSession.openChannel(EXEC_CHANNEL);
        channel.setCommand(remoteCommandExecutionRequest.getCommand().toString());
        channel.connect();

        channel.disconnect();
        secureShellSession.disconnect();
    }

}
