package weloveclouds.server.core;

import weloveclouds.communication.api.IConcurrentCommunicationApi;
import weloveclouds.communication.models.Connection;
import weloveclouds.server.models.ParsedMessage;
import weloveclouds.server.models.RequestFactory;
import weloveclouds.server.models.ResponseFactory;
import weloveclouds.server.models.responses.IResponse;
import weloveclouds.server.parsers.IMessageParser;

/**
 * Created by Benoit on 2016-10-29.
 */
public class SimpleConnectionHandler extends Thread implements IConnectionHandler{
    private IConcurrentCommunicationApi communicationApi;
    private RequestFactory requestFactory;
    private ResponseFactory responseFactory;
    private Connection connection;
    private IMessageParser messageParser;

    private SimpleConnectionHandler(SimpleConnectionBuilder simpleConnectionBuilder) {
        this.communicationApi = simpleConnectionBuilder.communicationApi;
        this.connection = simpleConnectionBuilder.connection;
        this.messageParser = simpleConnectionBuilder.messageParser;
    }

    @Override
    public void handleConnection() {
        start();
    }

    @Override
    public void run() {
        while(connection.isConnected()) {
            ParsedMessage parsedMessage = messageParser.parse(communicationApi.receiveFrom(connection));
            IResponse response = requestFactory.createRequestFromReceivedMessage(parsedMessage)
                    .execute();
            communicationApi.send(response.getBytes(), connection);
        }
    }


    public static class SimpleConnectionBuilder{
        private IConcurrentCommunicationApi communicationApi;
        private RequestFactory requestFactory;
        private ResponseFactory responseFactory;
        private Connection connection;
        private IMessageParser messageParser;

        public SimpleConnectionBuilder connection(Connection connection){
            this.connection = connection;
            return this;
        }

        public SimpleConnectionBuilder messageParser(IMessageParser messageParser){
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

        public SimpleConnectionBuilder communicationApi(IConcurrentCommunicationApi communicationApi){
            this.communicationApi = communicationApi;
            return this;
        }

        public SimpleConnectionHandler build(){
            return new SimpleConnectionHandler(this);
        }
    }
}
