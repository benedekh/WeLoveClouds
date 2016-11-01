package weloveclouds.server.models;

import java.net.Socket;

/**
 * Created by Benoit on 2016-10-29.
 */
public class Connection {
    Socket socket;

    public Connection(Socket socket) {
        this.socket = socket;
    }
}
