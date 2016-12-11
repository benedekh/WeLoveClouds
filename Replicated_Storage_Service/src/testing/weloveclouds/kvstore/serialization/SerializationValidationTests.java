package testing.weloveclouds.kvstore.serialization;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SerializationValidationTests {

    public static Test suite() {
        TestSuite clientSuite = new TestSuite("Replicated Storage Service SerializationTest-Suite");
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
        clientSuite.addTestSuite(KVAdminMessageTest.class);
        clientSuite.addTestSuite(KVMessageTest.class);
        clientSuite.addTestSuite(KVTransferMessageTest.class);
        return clientSuite;
    }
}
