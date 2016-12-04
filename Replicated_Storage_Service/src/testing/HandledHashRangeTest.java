package testing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import testing.util.KVServerInitializationUtil;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.api.v2.KVCommunicationApiV2;

/**
 * Tests to validate that the server only handles those keys which it is supposed to.
 * 
 * @author Benedek
 */
public class HandledHashRangeTest extends TestCase {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT = 50000;

    // hack because @BeforeClass, setUpBaseClass methods are not called by the TestSuite runner...
    private static boolean isTestPutACalled = false;
    private static boolean isTestPutBCalled = false;

    IKVCommunicationApiV2 serverCommunication;
    IMessageDeserializer<KVMessage, SerializedMessage> kvmessageDeserializer;
    IMessageSerializer<SerializedMessage, KVMessage> kvmessageSerializer;

    @BeforeClass
    public static void setUpBaseClass() {
        try (KVServerInitializationUtil initializationUtil = new KVServerInitializationUtil()) {
            initializationUtil.connect();
            initializationUtil.updateRingMetadataServerHandlesOnlyHashA();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownBaseClass() {
        try (KVServerInitializationUtil initializationUtil = new KVServerInitializationUtil()) {
            initializationUtil.connect();
            initializationUtil.updateRingMetadataServerHandlesEveryHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(SERVER_IP_ADDRESS).port(SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        serverCommunication =
                new KVCommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
        serverCommunication.connect();

        kvmessageDeserializer = new KVMessageDeserializer();
        kvmessageSerializer = new KVMessageSerializer();
    }

    @After
    public void tearDown() {
        serverCommunication.disconnect();
    }

    @Test
    public void testPutA() throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {
        manualSetup();
        KVMessage message =
                new KVMessage.Builder().status(StatusType.PUT).key("A").value("default").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.PUT_SUCCESS, response.getStatus());

        manualTearDown();
        isTestPutACalled = true;
    }

    @Test
    public void testPutB() throws UnableToSendContentToServerException, ConnectionClosedException,
            DeserializationException {
        manualSetup();
        KVMessage message =
                new KVMessage.Builder().status(StatusType.PUT).key("B").value("default").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.SERVER_NOT_RESPONSIBLE, response.getStatus());

        manualTearDown();
        isTestPutBCalled = true;
    }

    private void manualSetup() {
        if (!isTestPutACalled && !isTestPutBCalled) {
            setUpBaseClass();
        }
    }

    private void manualTearDown() {
        if (isTestPutACalled || isTestPutBCalled) {
            tearDownBaseClass();
        }
    }
}
