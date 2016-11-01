package testing;

import java.net.UnknownHostException;

import junit.framework.TestCase;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;
import weloveclouds.kvstore.communication.api.KVCommInterface;


public class ConnectionTest extends TestCase {


    public void testConnectionSuccess() {

        Exception ex = null;

        KVCommInterface kvClient = new KVCommunicationApiV1("localhost", 50000);
        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }

        assertNull(ex);
    }


    public void testUnknownHost() {
        Exception ex = null;
        KVCommInterface kvClient = new KVCommunicationApiV1("unknown", 50000);

        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex instanceof UnknownHostException);
    }


    public void testIllegalPort() {
        Exception ex = null;
        KVCommInterface kvClient = new KVCommunicationApiV1("localhost", 123456789);

        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }

        assertTrue(ex instanceof IllegalArgumentException);
    }



}

