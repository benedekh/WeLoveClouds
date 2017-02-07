package weloveclouds.commons.networking.socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.InitializationError;

/**
 * SSLContextHelper, used for managing trust and key managers, ssl contexts, keystores and SSL
 * Socket Factories.
 * 
 * @author Hunton, Benedek
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

    /**
     * @throws InitializationError if the SSLContext was not initialized correctly
     */
    private SSLContextHelper() {
        this.loadKeystore().initKMFactory().initTMFactory().initSSLContext();
    }

    /**
     * @return the only instance of this class (Singleton design pattern)
     */
    public static SSLContextHelper getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a socket and connects it to the specified port number at the specified address. This
     * socket is configured using the socket options established for this factory. If there is a
     * security manager, its checkConnect method is called with the host address and port as its
     * arguments. This could result in a SecurityException.
     * 
     * @param host the server host
     * @param port the server port
     * @return the {@link SSLSocket}
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public SSLSocket createSocket(InetAddress host, int port) throws IOException {
        return (SSLSocket) sslContext.getSocketFactory().createSocket(host, port);
    }

    /**
     * Returns a server socket bound to the specified port. The socket is configured with the socket
     * options (such as accept timeout) given to this factory. If there is a security manager, its
     * checkListen method is called with the port argument as its argument to ensure the operation
     * is allowed. This could result in a SecurityException.
     * 
     * @param port the port to listen to
     * @return the {@link SSLServerSocket}
     * @throws IOException for networking errors
     */
    public SSLServerSocket createServerSocket(int port) throws IOException {
        return (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(port);
    }

    private SSLContextHelper loadKeystore() {
        try {
            this.keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            this.keystore.load(new FileInputStream(PATHTOKEY), PASSPHRASE);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                | IOException e) {
            LOGGER.error(e);
            throw new InitializationError(
                    "SSLContextHelper could not be initialized, becase the keystore was not loaded.");
        }
        LOGGER.debug("Keystore loaded.");
        return this;
    }

    private SSLContextHelper initTMFactory() {
        try {
            this.trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            this.trustManagerFactory.init(keystore);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            LOGGER.error(e);
            throw new InitializationError(
                    "SSLContextHelper could not be initialized, becase the trust manager factory was not initialized.");
        }
        LOGGER.debug("Trust manager factory is initialized.");
        return this;
    }

    private SSLContextHelper initKMFactory() {
        try {
            this.keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            this.keyManagerFactory.init(keystore, PASSPHRASE);
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            LOGGER.error(e);
            throw new InitializationError(
                    "SSLContextHelper could not be initialized, becase the key manager factory was not initialized.");
        }
        LOGGER.debug("Key mananger factory instantiated and intialized");
        return this;
    }

    private SSLContextHelper initSSLContext() {
        try {
            this.sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagerFactory.getKeyManagers(),
                    trustManagerFactory.getTrustManagers(), null);
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            LOGGER.error(ex);
            throw new InitializationError(
                    "SSLContextHelper could not be initialized, becase SSLContext was not initialized.");
        }
        LOGGER.debug("SSL Context is initialized.");
        return this;
    }

}
