package testing;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import weloveclouds.communication.api.v1.IKVServerApi;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;
import weloveclouds.kvstore.models.IKVMessage;
import weloveclouds.kvstore.models.IKVMessage.StatusType;

public class InteractionTest extends TestCase {

    private IKVServerApi kvClient;

    @Before
    public void setUp() {
        //here we will use a socket with try with resources.
        kvClient = new KVCommunicationApiV1("localhost", 50000);
        try {
            kvClient.connect();
        } catch (Exception e) {
        }
    }
    
    
    public void tearDown() {
        kvClient.disconnect();
    }


    @Test
    public void testPut() {
        String key = "foobar";
        String value = "bar";
        IKVMessage response = null;
        Exception ex = null;
        try {
            response = kvClient.put(key, value);
        } catch (Exception e) {
            ex = e;
        }
        tearDown();
        assertTrue(ex == null && response.getStatus() == StatusType.PUT_SUCCESS);
    }

    @Test
    public void testPutDisconnected() {
        kvClient.disconnect();
        String key = "foo";
        String value = "bar";
        Exception ex = null;

        try {
            kvClient.put(key, value);
        } catch (Exception e) {
            ex = e;
        }

        tearDown();
        assertNotNull(ex);
    }

    @Test
    public void testUpdate() {
        String key = "updateTestValue";
        String initialValue = "initial";
        String updatedValue = "updated";

        IKVMessage response = null;
        Exception ex = null;

        try {
            kvClient.put(key, initialValue);
            response = kvClient.put(key, updatedValue);

        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex == null && response.getStatus() == StatusType.PUT_UPDATE
                && response.getValue().equals(updatedValue));
    }

    @Test
    public void testDelete() {
        String key = "deleteTestValue";
        String value = "toDelete";

        IKVMessage response = null;
        Exception ex = null;

        try {
            kvClient.put(key, value);
            response = kvClient.put(key, "null");

        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex == null && response.getStatus() == StatusType.DELETE_SUCCESS);
    }

    @Test
    public void testGet() {
        String key = "foo";
        String value = "bar";
        IKVMessage response = null;
        Exception ex = null;

        try {
            kvClient.put(key, value);
            response = kvClient.get(key);
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex == null && response.getValue().equals("bar"));
    }

    @Test
    public void testGetUnsetValue() {
        String key = "an unset value";
        IKVMessage response = null;
        Exception ex = null;

        try {
            response = kvClient.get(key);
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex == null && response.getStatus() == StatusType.GET_ERROR);
    }


}
