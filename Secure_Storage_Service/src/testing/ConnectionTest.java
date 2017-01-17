package testing;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.TestCase;
import weloveclouds.server.api.IKVServerApi;
import weloveclouds.server.api.v1.KVCommunicationApiV1;

/**
 * Test cases to test connection scenarios to the storage service.
 * 
 * @author Martin Jergler
 */
public class ConnectionTest extends TestCase {

    @Test
    public void testConnectionSuccess() {
        Exception ex = null;
        IKVServerApi kvClient = new KVCommunicationApiV1("localhost", 50000);

        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }

        assertNull(ex);
    }

    @Test
    public void testUnknownHost() {
        Exception ex = null;
        IKVServerApi kvClient = new KVCommunicationApiV1("unknown", 50000);

        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex instanceof UnknownHostException);
    }

    @Test
    public void testIllegalPort() {
        Exception ex = null;
        IKVServerApi kvClient = new KVCommunicationApiV1("localhost", 123456789);

        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex instanceof IllegalArgumentException);
    }

}

