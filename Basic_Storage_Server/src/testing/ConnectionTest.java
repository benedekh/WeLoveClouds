package testing;

import java.net.UnknownHostException;

import junit.framework.TestCase;
import weloveclouds.communication.api.v1.IKVServerApi;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;


public class ConnectionTest extends TestCase {


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

