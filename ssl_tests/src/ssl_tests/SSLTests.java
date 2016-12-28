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

public class SSLTests {
    @Test
    public void test() throws Exception {
        // path & password for the keystore
        String path = System.getProperty("user.home").concat("/Programming/keystore.jks");
        char[] passphrase = "fsesdfsdf".toCharArray();

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

        // create the socket factories
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        final SSLSocketFactory sslFactory = ctx.getSocketFactory();
        final SSLServerSocketFactory sslServerFactory = ctx.getServerSocketFactory();

        new Thread() {
            public void run() {
                try {
                    ServerSocket serverSocket = sslServerFactory.createServerSocket(8080);
                    Socket socket = serverSocket.accept();
                    byte[] response = new byte[256];
                    socket.getInputStream().read(response);
                    System.out.println("server thread: " + response[0]);
                    socket.close();
                    serverSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    Socket socket = sslFactory.createSocket("localhost", 8080);
                    socket.getOutputStream().write(new byte[] {42});
                    socket.close();
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
