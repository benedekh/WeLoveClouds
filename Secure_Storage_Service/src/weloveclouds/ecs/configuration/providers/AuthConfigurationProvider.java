package weloveclouds.ecs.configuration.providers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import weloveclouds.ecs.exceptions.authentication.InvalidAuthenticationInfosException;
import weloveclouds.ecs.models.ssh.AuthInfos;
import weloveclouds.ecs.models.ssh.AuthenticationMethod;

/**
 * Created by Benoit on 2016-11-16.
 */
public class AuthConfigurationProvider {
    private static final String AUTH_PROPERTIES_FILE_PATH = "./auth.properties";
    private static final String USERNAME_PROPERTIES = "username";
    private static final String PASSWORD_PROPERTIES = "password";
    private static final String PRIVATE_AUTH_KEY_PATH_PROPERTIES = "privateAuthKeyPath";
    private static AuthConfigurationProvider INSTANCE = null;

    private Properties properties;
    private AuthInfos authenticationInfos;

    private AuthConfigurationProvider() throws IOException, InvalidAuthenticationInfosException {
        loadConfiguration();
    }

    public String getUsername() {
        return authenticationInfos.getUsername();
    }

    public String getPassword() {
        return authenticationInfos.getPassword();
    }

    public String getAuthPrivateKeyFilePath() {
        return authenticationInfos.getPrivateKey();
    }

    public AuthenticationMethod getAuthenticactionMethod() {
        return this.authenticationInfos.getAuthenticationMethod();
    }

    public void reloadConfiguration() throws IOException, InvalidAuthenticationInfosException {
        loadConfiguration();
    }

    private void loadConfiguration() throws IOException, InvalidAuthenticationInfosException {
        try (FileInputStream authPropertiesFile = new FileInputStream(AUTH_PROPERTIES_FILE_PATH)) {
            properties = new Properties();
            properties.load(authPropertiesFile);
            authenticationInfos = new AuthInfos.AuthInfosBuilder()
                    .username(properties.getProperty(USERNAME_PROPERTIES))
                    .password(properties.getProperty(PASSWORD_PROPERTIES))
                    .privateKey(properties.getProperty(PRIVATE_AUTH_KEY_PATH_PROPERTIES))
                    .build();
        }
    }

    public static AuthConfigurationProvider getInstance() throws IOException, InvalidAuthenticationInfosException {
        if (INSTANCE == null) {
            INSTANCE = new AuthConfigurationProvider();
        }
        return INSTANCE;
    }
}
