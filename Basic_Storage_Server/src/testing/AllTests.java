package testing;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import app_kvServer.KVServer;
import junit.framework.Test;
import junit.framework.TestSuite;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.kvstore.serialization.KVMessageDeserializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.server.core.Server;
import weloveclouds.server.core.ServerSocketFactory;
import weloveclouds.server.models.requests.RequestFactory;
import weloveclouds.server.services.DataAccessService;
import weloveclouds.server.store.KVCache;
import weloveclouds.server.store.KVPersistentStorage;
import weloveclouds.server.store.cache.strategy.FIFOStrategy;
import weloveclouds.server.utils.LogSetup;


public class AllTests {
    private static int SERVER_PORT = 50000;
    private static int CACHE_SIZE = 10;
    private static Path ROOTH_PATH = Paths.get("./");
    
    static {
        try {
            new LogSetup("logs/testing/test.log", Level.ERROR);
            //new KVServer(50000, 10, "FIFO");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static {
        try {
            new Server.ServerBuilder()
                .port(SERVER_PORT)
                .serverSocketFactory(new ServerSocketFactory())
                .requestFactory(new RequestFactory(new DataAccessService(new KVCache
                        (CACHE_SIZE, new FIFOStrategy()), new KVPersistentStorage(ROOTH_PATH))))
                .communicationApiFactory(new CommunicationApiFactory())
                .messageSerializer(new KVMessageSerializer())
                .messageDeserializer(new KVMessageDeserializer())
                .build()
                .start();
        } catch (IOException e) {
            //Need to do logging here
        }
    }
    
    

	
	
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class); 
		clientSuite.addTestSuite(AdditionalTest.class); 
		clientSuite.addTestSuite(testing.weloveclouds.client.utils.ArgumentsValidatorTest.class);
		
		return clientSuite;
	}
	
}
