package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

import static weloveclouds.commons.serialization.models.XMLTokens.ACTIVE_CONNECTIONS;
import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.PRIORITY;
import static weloveclouds.commons.serialization.models.XMLTokens.SERVICE;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;

/**
 * A serializer which converts a {@link ServiceHealthInfos} to a {@link AbstractXMLNode}.
 * 
 * @author Benoit
 */
public class ServiceHealthInfosSerializer implements ISerializer<AbstractXMLNode, ServiceHealthInfos> {
    ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoISerializer;

    @Inject
    public ServiceHealthInfosSerializer(ISerializer<AbstractXMLNode, ServerConnectionInfo>
                                                serverConnectionInfoISerializer) {
        this.serverConnectionInfoISerializer = serverConnectionInfoISerializer;
    }

    @Override
    public AbstractXMLNode serialize(ServiceHealthInfos serviceHealthInfos) {
        return new XMLRootNode.Builder().token(SERVICE)
                .addInnerNode(new XMLNode(NAME, serviceHealthInfos.getServiceName()))
                .addInnerNode(new XMLNode(STATUS, serviceHealthInfos.getServiceStatus().name()))
                .addInnerNode(serverConnectionInfoISerializer
                        .serialize(serviceHealthInfos.getServiceEndpoint()))
                .addInnerNode(new XMLNode(ACTIVE_CONNECTIONS, String.valueOf(serviceHealthInfos
                        .getNumberOfActiveConnections())))
                .addInnerNode(new XMLNode(PRIORITY, String.valueOf(serviceHealthInfos.getServicePriority())))
                .build();
    }
}
