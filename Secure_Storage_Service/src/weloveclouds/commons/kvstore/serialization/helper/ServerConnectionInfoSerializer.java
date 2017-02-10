package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFO;
import static weloveclouds.commons.serialization.models.XMLTokens.IP_ADDRESS;
import static weloveclouds.commons.serialization.models.XMLTokens.PORT;

import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link ServerConnectionInfo} to a {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class ServerConnectionInfoSerializer
        implements ISerializer<AbstractXMLNode, ServerConnectionInfo> {

    @Override
    public AbstractXMLNode serialize(ServerConnectionInfo target) {
        Builder builder = new XMLRootNode.Builder().token(CONNECTION_INFO);

        if (target != null) {
            builder.addInnerNode(new XMLNode(IP_ADDRESS, target.getIpAddress().getHostAddress()))
                    .addInnerNode(new XMLNode(PORT, String.valueOf(target.getPort())));
        }

        return builder.build();
    }

}
