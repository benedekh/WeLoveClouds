package weloveclouds.commons.networking;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;

/**
 * SSLContextHelper, used for managing trust and key managers, ssl contexts, keystores and SSL
 * Socket Factories.
 * 
 * @author Hunton
 */
public class SSLContextHelper {

    private static final Logger LOGGER = Logger.getLogger(SSLContextHelper.class);
    private static final char[] PASSPHRASE = "weloveclouds".toCharArray();
    private static final String PATHTOKEY = "keystore.jks";

    private static final SSLContextHelper INSTANCE = new SSLContextHelper();

    private KeyStore keystore;
    private TrustManagerFactory trustManagerFactory;
    private KeyManagerFactory keyManagerFactory;
    private SSLContext sslContext;

    private SSLContextHelper() {
        this.loadKeystore().initKMFactory().initTMFactory().initSSLContext();
    }

    private SSLContextHelper loadKeystore() {
        try {
            this.keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            this.keystore.load(new FileInputStream(PATHTOKEY), PASSPHRASE);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        LOGGER.debug("Keystore loaded.");
        return this;
    }

    private SSLContextHelper initTMFactory() {
        try {
            this.trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            this.trustManagerFactory.init(keystore);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        LOGGER.debug("Trust manager factory instantiated and initialized");
        return this;
    }

    private SSLContextHelper initKMFactory() {
        try {
            this.keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            this.keyManagerFactory.init(keystore, PASSPHRASE);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        LOGGER.debug("Key mananger factory instantiated and intialized");
        return this;
    }

    private SSLContextHelper initSSLContext() {
        try {
            this.sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(), null);
            LOGGER.debug("SSL Context instantiated and initialized.");
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        return this;
    }

    public SSLContext getSSLContext() {
        return this.sslContext;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslContext.getSocketFactory();
    }

    public SSLServerSocketFactory getSSLServerSocketFactory() {
        return this.sslContext.getServerSocketFactory();
    }

    public static SSLContextHelper getInstance() {
        return INSTANCE;
    }
}
