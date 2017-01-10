package weloveclouds.commons.serialization.models;

import weloveclouds.commons.utils.StringUtils;

/**
 * Represents an abstract XML node.
 * 
 * @author Benoit
 */
public abstract class AbstractXMLNode {
    protected String token;

    protected AbstractXMLNode(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String toString() {
        return StringUtils.join("", "<", token, ">", getContentAsString(), "</", token, ">");
    }

    public abstract String getContentAsString();
}
