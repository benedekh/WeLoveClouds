package weloveclouds.server.requests.validator;

import java.net.InetAddress;
import java.util.Set;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.communication.models.ServerConnectionInfos;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.HashRanges;
import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.requests.kvclient.IKVClientRequest;
import weloveclouds.server.requests.kvecs.IKVECSRequest;
import weloveclouds.server.requests.kvserver.IKVServerRequest;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * Validates different objects which are used in the requests ({@link IKVClientRequest},
 * {@link IKVServerRequest}, {@link IKVECSRequest}} served by the KVServer.
 * 
 * @author Benedek
 */
public class KVServerRequestsValidator {
    private static final int KEY_SIZE_LIMIT_IN_BYTES = 20;
    private static final int VALUE_SIZE_LIMIT_IN_BYTES = 120 * 1000;

    private static final int NETWORK_PORT_LOWER_LIMIT = 0;
    private static final int NETWORK_PORT_UPPER_LIMIT = 65535;

    /**
     * A {@link KVEntry} key is invalid, if its size is bigger than 20 bytes.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateValueAsKVKey(String key) throws IllegalArgumentException {
        validateSize(key, KEY_SIZE_LIMIT_IN_BYTES);
    }

    /**
     * A {@link KVEntry} value is invalid, if its size is bigger than 120 kbytes.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateValueAsKVValue(String value) throws IllegalArgumentException {
        validateSize(value, VALUE_SIZE_LIMIT_IN_BYTES);
    }

    /**
     * Validates if the respective field's size as a byte array is smaller than the limit.
     * 
     * @throws IllegalArgumentException if there is a validation error
     */
    private static void validateSize(String field, int limit) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        byte[] key = field.getBytes(SerializedMessage.MESSAGE_ENCODING);
        if (key.length > limit) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * A {@link HashRanges} is valid, if it is not null and the encapsulated {@link HashRange}
     * instances are valid.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateHashRanges(HashRanges hashRanges) throws IllegalArgumentException {
        if (hashRanges == null) {
            throw new IllegalArgumentException();
        }

        Set<HashRange> ranges = hashRanges.getHashRanges();
        if (ranges == null || ranges.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (HashRange range : ranges) {
            validateHashRange(range);
        }
    }

    /**
     * A {@link HashRange} is valid, if it is not null.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateHashRange(HashRange range) throws IllegalArgumentException {
        if (range == null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * A {@link RingMetadata} is valid, if it is not null and the encapsulated
     * {@link RingMetadataPart} instances are valid.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateRingMetadata(RingMetadata ringMetadata)
            throws IllegalArgumentException {
        if (ringMetadata == null) {
            throw new IllegalArgumentException();
        }

        Set<RingMetadataPart> metadataParts = ringMetadata.getMetadataParts();
        if (metadataParts == null || metadataParts.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (RingMetadataPart metadataPart : metadataParts) {
            validateRingMetadataPart(metadataPart);
        }
    }

    /**
     * A {@link ServerConnectionInfos} is valid, if it is not null and the encapsulated
     * {@link ServerConnectionInfo} instances are valid.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateServerConnectionInfos(ServerConnectionInfos connectionInfos)
            throws IllegalArgumentException {
        if (connectionInfos == null) {
            throw new IllegalArgumentException();
        }

        Set<ServerConnectionInfo> serverConnectionInfos =
                connectionInfos.getServerConnectionInfos();
        if (serverConnectionInfos == null || serverConnectionInfos.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (ServerConnectionInfo serverConnectionInfo : serverConnectionInfos) {
            validateServerConnectionInfo(serverConnectionInfo);
        }
    }

    /**
     * A {@link RingMetadataPart} is valid, if:<br>
     * (1) it is not null,<br>
     * (2) neither the underlying hash range is null,<br>
     * (3)and the underlying {@link ServerConnectionInfo} is valid.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateRingMetadataPart(RingMetadataPart ringMetadataPart)
            throws IllegalArgumentException {
        if (ringMetadataPart == null) {
            throw new IllegalArgumentException();
        }
        validateHashRange(ringMetadataPart.getWriteRange());
        validateServerConnectionInfo(ringMetadataPart.getConnectionInfo());
    }

    /**
     * A MovableStorageUnits object is valid, if: <br>
     * (1) it is not null,<br>
     * (2) neither underlying stored movable storage unit is null, <br>
     * (3) neither keys stored in the respective movable storage unit is null.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateMovableStorageUnits(MovableStorageUnits storageUnits)
            throws IllegalArgumentException {
        if (storageUnits == null) {
            throw new IllegalArgumentException();
        }

        Set<MovableStorageUnit> movableStorageUnits = storageUnits.getStorageUnits();
        if (movableStorageUnits == null) {
            throw new IllegalArgumentException();
        }

        for (MovableStorageUnit storageUnit : movableStorageUnits) {
            Set<String> keySet = storageUnit.getKeys();
            if (keySet == null) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * A {@link ServerConnectionInfo} is valid, if:<br>
     * (1) it is not null,<br>
     * (2) neither the underlying IP address is null, <br>
     * (3) neither the underlying port is invalid (out of range [0, 65535]).
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    private static void validateServerConnectionInfo(ServerConnectionInfo connectionInfo)
            throws IllegalArgumentException {
        if (connectionInfo == null) {
            throw new IllegalArgumentException();
        }
        InetAddress ipAddress = connectionInfo.getIpAddress();
        if (ipAddress == null) {
            throw new IllegalArgumentException();
        }

        int port = connectionInfo.getPort();
        if (port < NETWORK_PORT_LOWER_LIMIT || port > NETWORK_PORT_UPPER_LIMIT) {
            throw new IllegalArgumentException();
        }
    }
}
