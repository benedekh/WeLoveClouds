package weloveclouds.commons.serialization;

import com.google.inject.Inject;

import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.NODE_HEALTH_INFOS;
import static weloveclouds.commons.serialization.models.XMLTokens.SERVICES;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;

/**
 * Created by Benoit on 2016-12-08.
 */
public class NodeHealthInfosSerializer implements ISerializer<AbstractXMLNode, NodeHealthInfos> {
    ISerializer<AbstractXMLNode, ServiceHealthInfos> serviceHealthInfosSerializer;

    @Inject
    public NodeHealthInfosSerializer(ISerializer<AbstractXMLNode, ServiceHealthInfos>
                                             serviceHealthInfosSerializer) {
        this.serviceHealthInfosSerializer = serviceHealthInfosSerializer;
    }

    @Override
    public AbstractXMLNode serialize(NodeHealthInfos nodeHealthInfos) {
        return new XMLRootNode.Builder()
                .token(NODE_HEALTH_INFOS)
                .addInnerNode(new XMLNode(NAME, nodeHealthInfos.getNodeName()))
                .addInnerNode(new XMLNode(STATUS, nodeHealthInfos.getNodeStatus().name()))
                .addInnerNode(serializeServicesHealthInfosFrom(nodeHealthInfos))
                .build();
    }

    public AbstractXMLNode serializeServicesHealthInfosFrom(NodeHealthInfos nodeHealthInfos) {
        XMLRootNode.Builder servicesHealthInfosXML = new XMLRootNode.Builder().token(SERVICES);

        for (ServiceHealthInfos serviceHealthInfos : nodeHealthInfos.getServicesHealthInfos()) {
            servicesHealthInfosXML.addInnerNode(serviceHealthInfosSerializer
                    .serialize(serviceHealthInfos));
        }

        return servicesHealthInfosXML.build();
    }
}
