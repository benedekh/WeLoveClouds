package testing;

import org.apache.log4j.Level;

import app_kvServer.KVServer;
import junit.framework.Test;
import junit.framework.TestSuite;
import testing.utils.KVServerInitializationUtils;
import testing.weloveclouds.client.utils.ArgumentsValidatorTest;
import testing.weloveclouds.client.utils.UserInputParserTest;
import testing.weloveclouds.ecs.utils.EcsArgumentsValidatorTest;
import testing.weloveclouds.kvstore.serialization.SerializationValidationTests;
import testing.weloveclouds.server.requests.validation.KVServerRequestFromKVClientValidationTests;
import weloveclouds.commons.utils.LogSetup;

public class AllTests {

    static {
        try (KVServerInitializationUtils initializationUtil = new KVServerInitializationUtils()) {
            new LogSetup("logs/testing/test.log", Level.ERROR);
            new KVServer(50000, 10, "FIFO");

            initializationUtil.connect();
            initializationUtil.initializeServerHandlesEveryHash();
            initializationUtil.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Test suite() {
        TestSuite clientSuite = new TestSuite("Secure Storage ServerTest-Suite");
        clientSuite.addTestSuite(ConnectionTest.class);
        clientSuite.addTestSuite(InteractionTest.class);
        clientSuite.addTestSuite(HandledHashRangeTest.class);
        clientSuite.addTestSuite(ArgumentsValidatorTest.class);
        clientSuite.addTestSuite(UserInputParserTest.class);
        clientSuite.addTestSuite(EcsArgumentsValidatorTest.class);
        clientSuite.addTest(SerializationValidationTests.suite());
        return clientSuite;
    }

}
