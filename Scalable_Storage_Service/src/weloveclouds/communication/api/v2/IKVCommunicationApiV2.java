package weloveclouds.communication.api.v2;

import weloveclouds.communication.api.v1.IKVCommunicationApi;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.hashing.models.RingMetadata;

public interface IKVCommunicationApiV2 extends IKVCommunicationApi {

    ServerConnectionInfo getServerConnectionInfo();

    void setRingMetadata(RingMetadata metadata);
}
