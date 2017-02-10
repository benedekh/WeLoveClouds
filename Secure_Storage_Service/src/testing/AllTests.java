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
import weloveclouds.commons.utils.LogSetup;

/**
 * Contains a test suite which covers every test case that shall be executed.
 * 
 * The test are advised to be executed from an IDE, which has a built-in JUnit plug-in.<br>
 * 
 * (1) Generate an SSL certificate using keytool with the following arguments: <br>
 * keytool -genkey -keystore keystore.jks -keyalg RSA<br>
 * (2) When generating the keystore (certificate) set both the certificate's and the keystore's
 * password for weloveclouds<br>
 * (3) Move the generated keystore.jks file to the root folder of the Secure_Storage_Service
 * project.<br>
 * (4) Set the working directory of JUnit for the Secure_Storage_Service's root folder in the
 * JUnit's run configuration.<br>
 * (5) Run the AllTests.java file with the aforementioned run configuration.
 * 
 * @author Benoit, Benedek, Hunton
 */
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
