package weloveclouds.commons.serialization.models;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Benoit on 2016-12-10.
 */
public class XMLRootNode extends AbstractXMLNode {
    private List<AbstractXMLNode> innerNodes;

    XMLRootNode(Builder xmlRootNodeBuilder) {
        super(xmlRootNodeBuilder.token);
        this.innerNodes = xmlRootNodeBuilder.innerNodes;
    }

    public String getContentAsString() {
        String stringRepresentation = "";
        for (AbstractXMLNode innerNode : innerNodes) {
            stringRepresentation += innerNode.toString();
        }
        return stringRepresentation;
    }

    public static class Builder {
        private String token;
        private List<AbstractXMLNode> innerNodes;

        public Builder() {
            this.innerNodes = new ArrayList<>();
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder addInnerNode(AbstractXMLNode xmlNode) {
            this.innerNodes.add(xmlNode);
            return this;
        }

        public AbstractXMLNode build() {
            return new XMLRootNode(this);
        }
    }
}
