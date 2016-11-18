package weloveclouds.ecs.configuration.providers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Benoit on 2016-11-16.
 */
public class AuthConfigurationProvider {
    private static final String AUTH_PROPERTIES_FILE = "auth.properties";
    private static final String USERNAME_PROPERTIES = "username";
    private static final String PRIVATE_AUTH_KEY_PATH = "privateAuthKeyPath";
    private static AuthConfigurationProvider INSTANCE = null;

    private Properties properties;

    private AuthConfigurationProvider() throws IOException {
        try (InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(AUTH_PROPERTIES_FILE)) {
            properties = new Properties();

            if (propertiesStream != null) {
                properties.load(propertiesStream);
            } else {
                throw new FileNotFoundException("property file '" + AUTH_PROPERTIES_FILE + "' not found in the " +
                        "classpath");
            }
        }
    }

    public String getUsername() {
        return properties.getProperty(USERNAME_PROPERTIES);
    }

    public String getAuthPrivateKeyFilePath() {
        return properties.getProperty(PRIVATE_AUTH_KEY_PATH);
    }

    public static AuthConfigurationProvider getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new AuthConfigurationProvider();
        }
        return INSTANCE;
    }
}
