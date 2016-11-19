package weloveclouds.ecs.services;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

import weloveclouds.ecs.exceptions.ssh.SecureShellServiceException;
import weloveclouds.ecs.models.commands.ssh.AbstractRemoteCommand;
import weloveclouds.ecs.models.ssh.AuthInfos;

import static weloveclouds.ecs.models.ssh.AuthenticationMethod.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class JshSecureShellService implements ISecureShellService {
    private static final int SECURE_SHELL_PORT = 22;
    private static final String EXEC_CHANNEL = "exec";
    private JSch secureShell;

    public JshSecureShellService() {
        this.secureShell = new JSch();
    }


    @Override
    public void runCommand(AbstractRemoteCommand command) throws SecureShellServiceException {
        String targettedHostIp = command.getTargettedHostIp();
        AuthInfos authInfos = command.getAuthenticationInfos();

        try {
            Session secureShellSession;

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            if (authInfos.getAuthenticationMethod() == PRIVATE_KEY) {
                secureShell.addIdentity(authInfos.getPrivateKey());
                secureShellSession = secureShell.getSession(authInfos.getUsername(), targettedHostIp,
                        SECURE_SHELL_PORT);
            } else {
                secureShellSession = secureShell.getSession(authInfos.getUsername(), targettedHostIp,
                        SECURE_SHELL_PORT);
                secureShellSession.setPassword(authInfos.getPassword());
            }

            secureShellSession.setConfig(config);

            secureShellSession.connect();
            ChannelExec channel = (ChannelExec) secureShellSession.openChannel(EXEC_CHANNEL);
            channel.setCommand(command.toString());
            channel.connect();

            channel.disconnect();
            secureShellSession.disconnect();
        } catch (JSchException e) {
            throw new SecureShellServiceException("Unable to execute command: " + command + " on " +
                    "targetted host: " + targettedHostIp, e);
        }
    }
}
