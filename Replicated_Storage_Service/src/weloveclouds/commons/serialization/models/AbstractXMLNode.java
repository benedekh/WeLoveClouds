package weloveclouds.commons.serialization.models;

import weloveclouds.commons.utils.StringUtils;

/**
 * Created by Benoit on 2016-12-10.
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
