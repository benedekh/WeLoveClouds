package weloveclouds.ecs.serialization;

import com.google.inject.Inject;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-08.
 */
public class NodeHealthInfosSerializer implements ISerializer<String, NodeHealthInfos> {
    private ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer;

    @Inject
    public NodeHealthInfosSerializer(ISerializer<String, ServerConnectionInfo> serverConnectionInfoISerializer) {
        this.serverConnectionInfoISerializer = serverConnectionInfoISerializer;
    }

    @Override
    public String serialize(NodeHealthInfos target) {
        return null;
    }
}
