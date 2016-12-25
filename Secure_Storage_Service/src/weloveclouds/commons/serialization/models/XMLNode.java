package weloveclouds.commons.serialization.models;

/**
 * Created by Benoit on 2016-12-10.
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