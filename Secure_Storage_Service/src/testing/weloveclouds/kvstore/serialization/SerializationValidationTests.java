package testing.weloveclouds.kvstore.serialization;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests suite to validate the serialization / deserialization scenarios.
 * 
 * @author Benedek
 */
public class SerializationValidationTests {

    public static Test suite() {
        TestSuite clientSuite = new TestSuite("Secure Storage Service SerializationTest-Suite");
        clientSuite.addTestSuite(HashTest.class);
        clientSuite.addTestSuite(HashRangeTest.class);
        clientSuite.addTestSuite(HashRangesSetTest.class);
        clientSuite.addTestSuite(MovableStorageUnitsSetTest.class);
        clientSuite.addTestSuite(MovableStorageUnitTest.class);
        clientSuite.addTestSuite(RingMetadataPartTest.class);
        clientSuite.addTestSuite(RingMetadataTest.class);
        clientSuite.addTestSuite(KVEntryTest.class);
        clientSuite.addTestSuite(ServerConnectionInfoTest.class);
        clientSuite.addTestSuite(ServerConnectionInfosSetTest.class);
        clientSuite.addTestSuite(UUIDTest.class);
        clientSuite.addTestSuite(KVAdminMessageTest.class);
        clientSuite.addTestSuite(KVMessageTest.class);
        clientSuite.addTestSuite(KVTransactionMessageTest.class);
        clientSuite.addTestSuite(TransferMessageTest.class);
        clientSuite.addTestSuite(KVTransferMessageTest.class);
        return clientSuite;
    }
}
