package ssl_tests;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.junit.Test;

public class ClientSSLConnectionTests {
    @Test
    public void test() throws Exception {
        //path and password for keystore
        String path = "../Secure_Storage_Service/keystore.jks";
        char[] passphrase = "weloveclouds".toCharArray();
        
        // load keystore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(path), passphrase);

        // initialize key manager
        KeyManagerFactory kmf =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, passphrase);

        // we sign the key ourselves
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        
        // we only need a server socket for this test
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        final SSLServerSocketFactory sslServerFactory = ctx.getServerSocketFactory();

        new Thread() {
            public void run() {
                try{
                    ServerSocket serverSocket = sslServerFactory.createServerSocket(50000);
                    Socket socket = serverSocket.accept();
                    /*
                    byte[] response = new byte[256];
                    socket.getInputStream().read(response);*/
                    System.out.println("Connection established!");
                    socket.close();
                    serverSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
        
        synchronized (this) {
            this.wait();
        }
    }
}
