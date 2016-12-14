package weloveclouds.commons.serialization.models;

import static weloveclouds.commons.utils.StringUtils.join;

/**
 * Created by Benoit on 2016-12-10.
 */
public abstract class AbstractXMLNode {
    protected String token;

    public AbstractXMLNode(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String toString() {
        return join("", "<", token, ">", getContentAsString(), "</", token, ">");
    }

    public abstract String getContentAsString();
}
