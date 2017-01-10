package weloveclouds.commons.serialization.models;

import java.util.ArrayList;
import java.util.List;

import weloveclouds.commons.utils.StringUtils;


/**
 * Represents an XML node which have child nodes.
 * 
 * @author Benoit
 */
public class XMLRootNode extends AbstractXMLNode {
    private List<AbstractXMLNode> innerNodes;

    protected XMLRootNode(Builder xmlRootNodeBuilder) {
        super(xmlRootNodeBuilder.token);
        this.innerNodes = xmlRootNodeBuilder.innerNodes;
    }

    public String getContentAsString() {
        if (!innerNodes.isEmpty()) {
            return StringUtils.join("", innerNodes);
        } else {
            return "";
        }
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
