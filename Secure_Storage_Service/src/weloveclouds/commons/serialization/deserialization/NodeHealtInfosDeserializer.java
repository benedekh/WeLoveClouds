package weloveclouds.commons.serialization.deserialization;

import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.SERVICE;
import static weloveclouds.commons.serialization.models.XMLTokens.SERVICES;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.inject.Inject;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.ecs.models.repository.NodeStatus;
import weloveclouds.loadbalancer.models.NodeHealthInfos;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

/**
 * A deserializer which converts a {@link String} to a {@link NodeHealthInfos}.
 * 
 * @author Benoit
 */
public class NodeHealtInfosDeserializer implements IDeserializer<NodeHealthInfos, String> {
    IDeserializer<ServiceHealthInfos, String> serviceHealthInfosDeserializer;

    @Inject
    public NodeHealtInfosDeserializer(
            IDeserializer<ServiceHealthInfos, String> serviceHealthInfosDeserializer) {
        this.serviceHealthInfosDeserializer = serviceHealthInfosDeserializer;
    }

    @Override
    public NodeHealthInfos deserialize(String serializedNodeHealthInfos)
            throws DeserializationException {
        try {
            return new NodeHealthInfos.Builder()
                    .nodeName(deserializeNameFrom(serializedNodeHealthInfos))
                    .nodeStatus(deserializeNodeStatusFrom(serializedNodeHealthInfos))
                    .servicesHealtInfos(
                            deserializeServicesHealthInfosFrom(serializedNodeHealthInfos))
                    .build();
        } catch (Exception e) {
            throw new DeserializationException(
                    "Unable to deserialize node health info: " + serializedNodeHealthInfos);
        }
    }

    private String deserializeNameFrom(String serializedNodeHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(NAME).matcher(serializedNodeHealthInfos);

        if (matcher.find()) {
            return matcher.group(XML_NODE);
        } else {
            throw new DeserializationException("Unable to deserialize node name from node "
                    + "health infos: " + serializedNodeHealthInfos);
        }
    }

    private NodeStatus deserializeNodeStatusFrom(String serializedNodeHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(STATUS).matcher(serializedNodeHealthInfos);

        if (matcher.find()) {
            try {
                return NodeStatus.valueOf(matcher.group(XML_NODE));
            } catch (IllegalArgumentException e) {
                throw new DeserializationException("Unable to deserialize node status from node"
                        + "health infos: " + serializedNodeHealthInfos);
            }
        } else {
            throw new DeserializationException("Unable to deserialize node status from node "
                    + "health infos: " + serializedNodeHealthInfos);
        }
    }

    private List<ServiceHealthInfos> deserializeServicesHealthInfosFrom(
            String serializedNodeHealthInfos) throws DeserializationException {
        Matcher servicesMatcher = getRegexFromToken(SERVICES).matcher(serializedNodeHealthInfos);
        List<ServiceHealthInfos> serviceHealthInfosList = new ArrayList<>();

        if (servicesMatcher.find()) {
            Matcher serviceMatcher =
                    getRegexFromToken(SERVICE).matcher(servicesMatcher.group(XML_NODE));
            while (serviceMatcher.find()) {
                String serializedService = serviceMatcher.group(XML_NODE);
                serviceHealthInfosList
                        .add(serviceHealthInfosDeserializer.deserialize(serializedService));
            }
        } else {
            throw new DeserializationException("Unable to deserialize services health infos from "
                    + ":" + serializedNodeHealthInfos);
        }
        return serviceHealthInfosList;
    }
}
