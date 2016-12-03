package weloveclouds.server.api.v2;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.server.api.IKVCommunicationApi;

/**
 * Second-generation {@link IKVCommunicationApi} which is capable of retrieving the connection
 * information to the most recent connection, and it can decide which server to send the request to
 * based on the {@link RingMetadata} information.
 *
 * @author Benedek
 */
public interface IKVCommunicationApiV2 extends IKVCommunicationApi {

    /**
     * @return the most recent connection information
     */
    public ServerConnectionInfo getServerConnectionInfo();

    /**
     * Save the most recent metadata information about which server handles which hash ranges, in
     * order to decide which key shall be forwarded to which server.
     */
    public void setRingMetadata(RingMetadata metadata);
}
