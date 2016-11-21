package weloveclouds.ecs.services;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.util.Properties;

import weloveclouds.ecs.configuration.providers.AuthConfigurationProvider;
import weloveclouds.ecs.exceptions.authentication.InvalidAuthenticationInfosException;
import weloveclouds.ecs.exceptions.ssh.SecureShellServiceException;
import weloveclouds.ecs.models.commands.internal.ssh.AbstractRemoteCommand;

import static weloveclouds.ecs.models.ssh.AuthenticationMethod.*;

/**
 * Created by Benoit on 2016-11-16.
 */
public class JshSecureShellService implements ISecureShellService {
    private static final int SECURE_SHELL_PORT = 22;
    private static final String EXEC_CHANNEL = "exec";
    private AuthConfigurationProvider authConfigurationProvider;
    private JSch secureShell;

    public JshSecureShellService() throws IOException, InvalidAuthenticationInfosException {
        this.authConfigurationProvider = AuthConfigurationProvider.getInstance();
        this.secureShell = new JSch();
    }


    @Override
    public void runCommand(AbstractRemoteCommand command) throws SecureShellServiceException {
        String targettedHostIp = command.getTargettedHostIp();

        try {
            Session secureShellSession;

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            if (authConfigurationProvider.getAuthenticactionMethod() == PRIVATE_KEY) {
                secureShell.addIdentity(authConfigurationProvider.getAuthPrivateKeyFilePath());
                secureShellSession = secureShell.getSession(authConfigurationProvider.getUsername(), targettedHostIp,
                        SECURE_SHELL_PORT);
            } else {
                secureShellSession = secureShell.getSession(authConfigurationProvider.getUsername(), targettedHostIp,
                        SECURE_SHELL_PORT);
                secureShellSession.setPassword(authConfigurationProvider.getPassword());
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
