package weloveclouds.commons.serialization.models;

/**
 * Represents an XML node.
 * 
 * @author Benoit
 */
public class XMLNode extends AbstractXMLNode {
    private String content;

    public XMLNode(String token, String content) {
        super(token);
        this.content = content;
    }

    @Override
    public String getContentAsString() {
        return content;
    }
}
