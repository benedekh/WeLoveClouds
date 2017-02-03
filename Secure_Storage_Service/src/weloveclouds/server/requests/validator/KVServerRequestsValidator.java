package weloveclouds.server.requests.validator;

import java.net.InetAddress;
import java.util.Set;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.requests.kvclient.IKVClientRequest;
import weloveclouds.server.requests.kvecs.IKVECSRequest;
import weloveclouds.server.requests.kvserver.transfer.IKVTransferRequest;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * Validates different objects which are used in the requests ({@link IKVClientRequest},
 * {@link IKVTransferRequest}, {@link IKVECSRequest}) served by the KVServer.
 * 
 * @author Benedek
 */
public class KVServerRequestsValidator {
    private static final int KEY_SIZE_LIMIT_IN_BYTES = 20;
    private static final int VALUE_SIZE_LIMIT_IN_BYTES = 120 * 1000;

    private static final int NETWORK_PORT_LOWER_LIMIT = 0;
    private static final int NETWORK_PORT_UPPER_LIMIT = 65535;

    /**
     * A {@link KVEntry} key is invalid, if its size is bigger than
     * {@value #KEY_SIZE_LIMIT_IN_BYTES} bytes.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateValueAsKVKey(String key) throws IllegalArgumentException {
        validateSize(key, KEY_SIZE_LIMIT_IN_BYTES);
    }

    /**
     * A {@link KVEntry} value is invalid, if its size is bigger than
     * {@value #VALUE_SIZE_LIMIT_IN_BYTES} bytes.
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
     * HashRanges are valid, if its set is not null and the encapsulated {@link HashRange} instances
     * are valid.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    private static void validateHashRanges(Set<HashRange> hashRanges)
            throws IllegalArgumentException {
        if (hashRanges == null) {
            throw new IllegalArgumentException();
        }
        for (HashRange range : hashRanges) {
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
     * ServerConnectionInfos are valid, if its set is not null and the encapsulated
     * {@link ServerConnectionInfo} instances are valid.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateServerConnectionInfos(Set<ServerConnectionInfo> connectionInfos)
            throws IllegalArgumentException {
        if (connectionInfos == null) {
            throw new IllegalArgumentException();
        }
        for (ServerConnectionInfo serverConnectionInfo : connectionInfos) {
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
        Set<HashRange> readRanges = ringMetadataPart.getReadRanges();
        if (readRanges != null) {
            validateHashRanges(readRanges);
        }

        HashRange writeRange = ringMetadataPart.getWriteRange();
        if (writeRange != null) {
            validateHashRange(writeRange);
        }
        validateServerConnectionInfo(ringMetadataPart.getConnectionInfo());
    }

    /**
     * MovableStorageUnits are valid, if: <br>
     * (1) its set is not null,<br>
     * (2) neither underlying stored movable storages units are null, <br>
     * (3) neither keys stored in the respective movable storage unit is null.
     * 
     * @throws IllegalArgumentException if a validation error occurs
     */
    public static void validateMovableStorageUnits(Set<MovableStorageUnit> storageUnits)
            throws IllegalArgumentException {
        if (storageUnits == null) {
            throw new IllegalArgumentException();
        }

        for (MovableStorageUnit storageUnit : storageUnits) {
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
     * (3) neither the underlying port is invalid (out of range [{@value #NETWORK_PORT_LOWER_LIMIT},
     * {@value #NETWORK_PORT_UPPER_LIMIT}]).
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
