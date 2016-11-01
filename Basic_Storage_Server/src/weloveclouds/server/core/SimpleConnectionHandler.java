package weloveclouds.server.core;

import jdk.nashorn.internal.ir.RuntimeNode;
import weloveclouds.server.models.Connection;
import weloveclouds.server.models.RequestFactory;
import weloveclouds.server.models.ResponseFactory;
import weloveclouds.server.parsers.IParser;

/**
 * Created by Benoit on 2016-10-29.
 */
public class SimpleConnectionHandler extends Thread implements IConnectionHandler{
    private Connection connection;
    private IParser messageParser;

    private SimpleConnectionHandler(SimpleConnectionBuilder simpleConnectionBuilder) {
        this.connection = simpleConnectionBuilder.connection;
        this.messageParser = simpleConnectionBuilder.messageParser;
    }

    @Override
    public void handleConnection() {
        start();
    }

    @Override
    public void run() {

    }


    public static class SimpleConnectionBuilder{
        private RequestFactory requestFactory;
        private ResponseFactory responseFactory;
        private Connection connection;
        private IParser messageParser;

        public SimpleConnectionBuilder connection(Connection connection){
            this.connection = connection;
            return this;
        }

        public SimpleConnectionBuilder messageParser(IParser messageParser){
            this.messageParser = messageParser;
            return this;
        }

        public SimpleConnectionBuilder requestFactory(RequestFactory requestFactory){
            this.requestFactory = requestFactory;
            return this;
        }

        public SimpleConnectionBuilder responseFactory(ResponseFactory responseFactory){
            this.responseFactory = responseFactory;
            return this;
        }

        public SimpleConnectionHandler build(){
            return new SimpleConnectionHandler(this);
        }
    }
}
