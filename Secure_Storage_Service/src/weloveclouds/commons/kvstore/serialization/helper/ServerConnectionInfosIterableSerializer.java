package weloveclouds.commons.kvstore.serialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFO;
import static weloveclouds.commons.serialization.models.XMLTokens.CONNECTION_INFOS;

import weloveclouds.commons.serialization.ISerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLNode;
import weloveclouds.commons.serialization.models.XMLRootNode;
import weloveclouds.commons.serialization.models.XMLRootNode.Builder;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A serializer which converts a {@link Iterable<ServerConnectionInfo>} to a
 * {@link AbstractXMLNode}.
 * 
 * @author Benedek, Hunton
 */
public class ServerConnectionInfosIterableSerializer
        implements ISerializer<AbstractXMLNode, Iterable<ServerConnectionInfo>> {

    private ISerializer<AbstractXMLNode, ServerConnectionInfo> serverConnectionInfoSerializer =
            new ServerConnectionInfoSerializer();

    @Override
    public AbstractXMLNode serialize(Iterable<ServerConnectionInfo> target) {
        Builder builder = new XMLRootNode.Builder().token(CONNECTION_INFOS);

        if (target != null) {
            for (ServerConnectionInfo connectionInfo : target) {
                builder.addInnerNode(new XMLNode(CONNECTION_INFO,
                        serverConnectionInfoSerializer.serialize(connectionInfo).toString()));
            }
        }

        return builder.build();
    }

}
