package weloveclouds.ecs.models.ssh;

import java.io.IOException;

import weloveclouds.ecs.configuration.providers.AuthConfigurationProvider;
import weloveclouds.ecs.exceptions.authentication.InvalidAuthenticationInfosException;
import weloveclouds.ecs.services.ISecureShellService;
import weloveclouds.ecs.services.JshSecureShellService;

/**
 * Created by Benoit on 2016-11-23.
 */
public class SecureShellServiceFactory {
    private AuthConfigurationProvider authConfigurationProvider;

    public SecureShellServiceFactory() throws IOException, InvalidAuthenticationInfosException {
        this.authConfigurationProvider = AuthConfigurationProvider.getInstance();
    }

    public ISecureShellService createJshSecureShellService() {
        return new JshSecureShellService(authConfigurationProvider);
    }
}
