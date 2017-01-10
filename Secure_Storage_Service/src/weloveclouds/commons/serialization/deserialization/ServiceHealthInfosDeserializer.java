package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.commons.status.ServiceStatus;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.ServiceHealthInfos;

import static weloveclouds.commons.serialization.models.XMLTokens.ACTIVE_CONNECTIONS;
import static weloveclouds.commons.serialization.models.XMLTokens.NAME;
import static weloveclouds.commons.serialization.models.XMLTokens.PRIORITY;
import static weloveclouds.commons.serialization.models.XMLTokens.STATUS;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

/**
 * A deserializer which converts a {@link String} to a {@link ServiceHealthInfos}.
 * 
 * @author Benoit
 */
public class ServiceHealthInfosDeserializer implements IDeserializer<ServiceHealthInfos, String> {
    IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer;

    @Inject
    public ServiceHealthInfosDeserializer(
            IDeserializer<ServerConnectionInfo, String> serverConnectionInfoDeserializer) {
        this.serverConnectionInfoDeserializer = serverConnectionInfoDeserializer;
    }

    @Override
    public ServiceHealthInfos deserialize(String serializedServiceInfos)
            throws DeserializationException {
        try {
            return new ServiceHealthInfos.Builder()
                    .serviceName(deserializeServiceNameFrom(serializedServiceInfos))
                    .serviceStatus(deserializeServiceStatusFrom(serializedServiceInfos))
                    .servicePriority(deserializeServicePriorityFrom(serializedServiceInfos))
                    .serviceEnpoint(
                            serverConnectionInfoDeserializer.deserialize(serializedServiceInfos))
                    .numberOfActiveConnections(
                            deserializeNumberOfActiveConnections(serializedServiceInfos))
                    .build();
        } catch (Exception e) {
            throw new DeserializationException("Unable to deserialize service health info from: "
                    + serializedServiceInfos + "with cause: " + e.getMessage());
        }
    }

    private String deserializeServiceNameFrom(String serializedServiceHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(NAME).matcher(serializedServiceHealthInfos);

        if (matcher.find()) {
            return matcher.group(XML_NODE);
        } else {
            throw new DeserializationException("Unable to deserialize service name from "
                    + "service health infos: " + serializedServiceHealthInfos);
        }
    }

    private ServiceStatus deserializeServiceStatusFrom(String serializedServiceHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(STATUS).matcher(serializedServiceHealthInfos);

        if (matcher.find()) {
            return ServiceStatus.valueOf(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize service status from "
                    + "service health infos: " + serializedServiceHealthInfos);
        }
    }

    private int deserializeServicePriorityFrom(String serializedServiceHealthInfos)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(PRIORITY).matcher(serializedServiceHealthInfos);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize service priority from "
                    + "service health infos: " + serializedServiceHealthInfos);
        }
    }

    private int deserializeNumberOfActiveConnections(String serializedServiceHealthInfos)
            throws DeserializationException {
        Matcher matcher =
                getRegexFromToken(ACTIVE_CONNECTIONS).matcher(serializedServiceHealthInfos);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(XML_NODE));
        } else {
            throw new DeserializationException("Unable to deserialize number of connections from "
                    + "service health infos: " + serializedServiceHealthInfos);
        }
    }
}
