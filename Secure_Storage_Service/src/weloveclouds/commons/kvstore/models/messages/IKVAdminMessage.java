package weloveclouds.commons.kvstore.models.messages;

import java.util.Set;

import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * Represents an administrative message between the ECS and the KVServer.
 * 
 * @author Martin Jergler, Hunton
 */
public interface IKVAdminMessage {

    enum StatusType {
        INITKVSERVER, /* Initialize the server with the context - request */
        START, /* StartNode the server for the client - request */
        STOP, /* Stop the server, but keep it alive - request */
        SHUTDOWN, /* Shut down the server - request */
        LOCKWRITE, /* Lock the write operation on the server - request */
        UNLOCKWRITE, /* Unlock the write lock on the server - request */
        COPYDATA, /* Copy range of the data from one server to another - request */
        MOVEDATA, /* Move range of the data from one server to another - request */
        REMOVERANGE, /* Remove range from the server - request */
        UPDATE, /* Update the metadata structure - request */
        RESPONSE_SUCCESS, /* Request was executed successfully. */
        RESPONSE_ERROR/* There was an error during the execution. */
    }

    /**
     * @return a status string that is used to identify request types, response types and error
     * types associated to the message
     */
    StatusType getStatus();

    /**
     * @return the ring metadata parts (<IP, port, <range, role>>) for each server
     */
    RingMetadata getRingMetadata();

    /**
     * @return the IP+port of the target server to which entries denoted by the encapsulated
     * HashRange have to be transferred
     */
    RingMetadataPart getTargetServerInfo();

    /**
     * @return connection information about the replicas, e.g. on which IP + port are they
     * accessible
     */
    Set<ServerConnectionInfo> getReplicaConnectionInfos();

    /**
     * @return the range whose entries shall be removed from the server
     */
    HashRange getRemovableRange();

    /**
     * @return if the message is a response then the message text can be obtained here
     */
    String getResponseMessage();
}
