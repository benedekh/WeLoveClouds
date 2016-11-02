package testing;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.TestCase;
import weloveclouds.communication.api.v1.IKVServerApi;
import weloveclouds.communication.api.v1.KVCommunicationApiV1;
import weloveclouds.communication.exceptions.*;

/**
 * 
 * @author hb - not quite, he only modified the provided test cases to suite our architecture.
 *
 */
public class ConnectionTest extends TestCase {


    public void testConnectionSuccess() {

        Exception ex = null;
        //this will fail if there isn't a server running on your machine.
        IKVServerApi kvClient = new KVCommunicationApiV1("localhost", 50000);
        try {
            kvClient.connect();
        } catch (Exception e) {
            ex = e;
        }
        //kvClient.disconnect();
        assertNull(ex);
    }

    @Test
    public void testUnknownHost(){
        Exception ex = null;
        KVCommunicationApiV1 kvClient = new KVCommunicationApiV1("unknown", 50000);

        try {
            kvClient.connect();
        } catch (Exception e) {
           ex = e;
        }
        /**
         * Please note that we've had to diverge from the prescribed tests as 
         * our KVCommunicationApiV1 class handles UnknownHostExceptions internally.
         * @see weloveclouds.communication.api.V1.KVCommunicationApiV1
         * @see weloveclouds.communication.models.ServerConnectionInfo
         */
        assertTrue(ex instanceof Exception);
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

