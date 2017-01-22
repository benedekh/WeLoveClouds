package weloveclouds.ecs.services;

import static weloveclouds.ecs.models.ssh.AuthenticationMethod.PRIVATE_KEY;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.configuration.providers.AuthConfigurationProvider;
import weloveclouds.ecs.exceptions.ssh.SecureShellServiceException;
import weloveclouds.ecs.models.commands.internal.ssh.AbstractRemoteCommand;

/**
 * Created by Benoit on 2016-11-16.
 */
public class JshSecureShellService implements ISecureShellService {
    private static final Logger LOGGER = Logger.getLogger(JshSecureShellService.class);

    private static final int SUCCESS = 0;
    private static final int IN_PROGRESS = -1;
    private static final int SECURE_SHELL_PORT = 22;
    private static final String EXEC_CHANNEL = "exec";
    private static final Duration EXECUTION_TIME_FAILURE_THRESHOLD = new Duration(3000);
    private AuthConfigurationProvider authConfigurationProvider;

    public JshSecureShellService(AuthConfigurationProvider authConfigurationProvider) {
        this.authConfigurationProvider = authConfigurationProvider;
    }

    @Override
    synchronized public void runCommand(AbstractRemoteCommand command) throws SecureShellServiceException {
        String targetedHostIp = command.getTargetedHostIp();
        String errorMessage = StringUtils.join(" ", "Unable to execute command:", command, "on " +
                "targeted host:", targetedHostIp);
        JSch secureShell = new JSch();

        try {
            Session secureShellSession;

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            if (authConfigurationProvider.getAuthenticactionMethod() == PRIVATE_KEY) {
                secureShell.addIdentity(authConfigurationProvider.getAuthPrivateKeyFilePath());
                secureShellSession = secureShell.getSession(authConfigurationProvider.getUsername(), targetedHostIp,
                        SECURE_SHELL_PORT);
            } else {
                secureShellSession = secureShell.getSession(authConfigurationProvider.getUsername(), targetedHostIp,
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
            throw new SecureShellServiceException(errorMessage + " with cause: " + e.getMessage()
                    , e);
        }
    }
}
