package weloveclouds.commons.networking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;

import weloveclouds.commons.utils.StringUtils;

/**
 * SSLContextHelper, used for managing trust and key managers, ssl contexts, keystores and SSL Socket
 * Factories.
 * @author hb
 *
 */
public class SSLContextHelper {

    //This is a singleton class 
    private static class Holder{
        private static SSLContextHelper INSTANCE = new SSLContextHelper();
    }
    
    private static final Logger LOGGER = Logger.getLogger(SSLContextHelper.class);
    private KeyStore keystore;
    private TrustManagerFactory trustManagerFactory;
    private KeyManagerFactory keyManagerFactory;
    private SSLContext sslContext;
    private static final char[] PASSPHRASE = "weloveclouds".toCharArray();
    /*this path will be temporary until I work out a better way of
     * storing the key.
     */
    private static final String PATHTOKEY = "keystore.jks";
    
    private SSLContextHelper(){
        this.loadKeystore().initKMFactory().initTMFactory().initSSLContext();
    }
    
    private SSLContextHelper loadKeystore(){
        try {
            this.keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            this.keystore.load(new FileInputStream(PATHTOKEY), PASSPHRASE);
        } catch (KeyStoreException e) {
            LOGGER.error(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        } catch (CertificateException e) {
            LOGGER.error(e.getMessage());
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.debug("Keystore loaded.");
        return this;
    }
    
    private SSLContextHelper initTMFactory(){
        try {
            this.trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            this.trustManagerFactory.init(this.keystore);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        } catch (KeyStoreException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.debug("Trust manager factory instantiated and initialized");
        return this;
    }
    
    private SSLContextHelper initKMFactory(){
        try {
            this.keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            this.keyManagerFactory.init(this.keystore, PASSPHRASE);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        } catch (UnrecoverableKeyException e) {
            LOGGER.error(e.getMessage());
        } catch (KeyStoreException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.debug("Key mananger factory instantiated and intialized");
        return this;
    }
    
    private SSLContextHelper initSSLContext(){
        try {
            this.sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            LOGGER.debug("SSL Context instantiated and initialized.");
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(ex.getMessage());
        } catch (KeyManagementException ex) {
            LOGGER.error(ex.getMessage());
        }
        return this;
    }
    
    public SSLContext getSSLContext(){
        return this.sslContext;
    }
    
    public SSLSocketFactory getSSLSocketFactory(){
        return this.sslContext.getSocketFactory();
    }
    
    public SSLServerSocketFactory getSSLServerSocketFactory(){
        return this.sslContext.getServerSocketFactory();
    }
    
    public static SSLContextHelper getInstance(){
        return Holder.INSTANCE;
    }
}
